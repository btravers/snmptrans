package com.zenika.snmptrans.model.output;

import com.zenika.snmptrans.utils.AppContext;
import com.zenika.snmptrans.model.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class BluefloodWriter implements OutputWriter {

    private final static int DEFAULT_TTL = 2592000;

    private String host;
    private Integer port;
    private Integer ttl;

    private HttpClientConnectionManager httpClientConnectionManager = AppContext.getApplicationContext().getBean(HttpClientConnectionManager.class);;

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
    public void doWrite(Server server, Collection<Result> results) throws IOException {
        String url = "http://" + host + ":" + port + "/v2.0/jmx/ingest";

        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(this.httpClientConnectionManager);

        HttpClient httpClient = httpClientBuilder.build();
        HttpPost request = new HttpPost(url);

        String body = this.bodyRequest(server, results);
        StringEntity params = new StringEntity(body);
        request.addHeader("content-type", "application/x-www-form-urlencoded");
        request.setEntity(params);
        httpClient.execute(request);
    }

    public String bodyRequest(Server server, Collection<Result> results) {
        String body = "[";

        for (Result result : results) {
            String name = new StringBuilder()
                    .append(server.getHost().replace(".", "_"))
                    .append("_")
                    .append(server.getPort())
                    .append(".")
                    .append(result.getOid().replace(".", "_"))
                    .toString();
            long timestamp = result.getTimestamp();
            Object value = result.getValue();

            String line = "{ \"metricName\": \"" + name + "\", \"metricValue\": " + value + ", \"collectionTime\": " + timestamp
                    + ", \"ttlInSeconds\": " + this.ttl + "},";
            body += line;
        }

        if (body.length() > 1) {
            body = body.substring(0, body.length() - 1);
        }
        body += "]";

        return body;
    }
}
