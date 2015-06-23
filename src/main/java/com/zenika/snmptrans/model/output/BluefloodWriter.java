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
import java.util.Collection;
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
    public void doWrite(Map<String, String> results, Map<String, OIDInfo> oidInfo, long timestamp) {
        String url = "http://" + host + ":" + port + "/v2.0/jmx/ingest";

        try {
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(this.httpClientConnectionManager);

            HttpClient httpClient = httpClientBuilder.build();
            HttpPost request = new HttpPost(url);


            String body = "[";

            for (Map.Entry<String, String> result : results.entrySet()) {
                OIDInfo info = oidInfo.get(result.getKey());

                String name = new StringBuilder()
                        .append(info.getAgent())
                        .append(".")
                        .append(info.getAlias())
                        .append(".")
                        .append(info.getName())
                        .append(".")
                        .append(info.getAttr())
                        .toString();

                String line = "{ \"metricName\": \"" + name + "\", \"metricValue\": " + result.getValue() + ", \"collectionTime\": " + timestamp / 1000
                        + ", \"ttlInSeconds\": " + this.ttl + "},";
                body += line;

                logger.info("New entry: " + line);
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
