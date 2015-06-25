package com.zenika.snmptrans.model.output;

import com.zenika.snmptrans.utils.AppContext;
import com.zenika.snmptrans.model.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class BluefloodWriter implements Writer {
    private static final Logger logger = LoggerFactory.getLogger(BluefloodWriter.class);

    private final static int DEFAULT_TTL = 2592000;

    private String host;
    private Integer port;
    private Integer ttl;

    private HttpClientConnectionManager httpClientConnectionManager = AppContext.getApplicationContext().getBean(HttpClientConnectionManager.class);
    ;

    @Override
    public void setSettings(Map<String, Object> settings) throws ValidationException {
        for (Map.Entry<String, Object> setting : settings.entrySet()) {
            switch (setting.getKey()) {
                case "host":
                    this.host = (String) setting.getValue();
                    break;
                case "port":
                    this.port = (Integer) setting.getValue();
                    break;
                case "ttl":
                    this.ttl = (Integer) setting.getValue();
                    break;
                default:
                    throw new ValidationException(String.format("Unexpected field %s for Blueflood writer", setting.getKey()));
            }
        }

        if (this.host == null) {
            throw new ValidationException("Missing host setting");
        }

        if (this.port == null) {
            throw new ValidationException("Missing port setting");
        }

        if (this.ttl == null) {
            this.ttl = DEFAULT_TTL;
        }
    }

    @Override
    public void doWrite(Map<String, Map<String, Map<String, String>>> results, SnmpProcess snmpProcess, long timestamp) {
        String url = "http://" + host + ":" + port + "/v2.0/jmx/ingest";

        try {
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(this.httpClientConnectionManager);

            HttpClient httpClient = httpClientBuilder.build();
            HttpPost request = new HttpPost(url);

            String agent = new StringBuilder()
                    .append(snmpProcess.getServer().getHost().replace('.', '_').replace(" ", ""))
                    .append('_')
                    .append(snmpProcess.getServer().getPort())
                    .toString();


            String body = "[";

            for (Query query : snmpProcess.getQueries()) {

                String name = new StringBuilder()
                        .append(agent)
                        .append(".")
                        .toString();

                if (query.getResultAlias() == null) {
                    name = new StringBuilder()
                            .append(name)
                            .append(query.getObj().replace(".", "_").replace(" ", ""))
                            .append(".")
                            .toString();
                } else {
                    name = new StringBuilder()
                            .append(query.getResultAlias().replace(".", "_").replace(" ", "_"))
                            .append(".")
                            .toString();
                }


                Map<String, Map<String, String>> queryResults = results.get(query.getObj());

                if (queryResults == null) {
                    continue;
                }

                Map<String, String> nameResults = null;
                if (query.getTypeName() != null) {
                    nameResults = queryResults.get(query.getTypeName());
                }

                for (Attribute attr : query.getAttr()) {
                    Map<String, String> attrResults = queryResults.get(attr.getValue());

                    for (Map.Entry<String, String> attrResult : attrResults.entrySet()) {
                        String mectricName = new StringBuilder()
                                .append(name)
                                .append(".")
                                .toString();

                        if (nameResults != null) {
                            mectricName = new StringBuilder()
                                    .append(mectricName)
                                    .append(nameResults.get(attrResult.getKey()))
                                    .toString();
                        } else {
                            mectricName = new StringBuilder()
                                    .append(mectricName)
                                    .append(attrResult.getKey())
                                    .toString();
                        }

                        if (attr.getAlias() != null) {
                            mectricName = new StringBuilder()
                                    .append(mectricName)
                                    .append(".")
                                    .append(attr.getAlias().replace(".", "_").replace(" ", "_"))
                                    .toString();
                        } else {
                            mectricName = new StringBuilder()
                                    .append(mectricName)
                                    .append(".")
                                    .append(attr.getValue().replace(".", "_").replace(" ", ""))
                                    .toString();
                        }

                        String line = new StringBuilder()
                                .append("{ \"metricName\": \"")
                                .append(mectricName)
                                .append("\", \"metricValue\": ")
                                .append(attrResult.getValue())
                                .append(", \"collectionTime\": ")
                                .append(timestamp / 1000)
                                .append(", \"ttlInSeconds\": " + this.ttl + "},")
                                .toString();

                        logger.info("New entry: " + line);

                        body = new StringBuilder()
                                .append(body)
                                .append(line)
                                .toString();
                    }

                }
            }

            if (body.length() > 1) {
                body = body.substring(0, body.length() - 1);
            }
            body += "]";


            StringEntity params = new StringEntity(body);
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(params);

            httpClient.execute(request);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
