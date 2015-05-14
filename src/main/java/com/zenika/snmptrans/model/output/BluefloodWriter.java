package com.zenika.snmptrans.model.output;

import com.zenika.snmptrans.model.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class BluefloodWriter implements OutputWriter {

    private String host;
    private Integer port;
    private Long ttl;

    @Autowired
    private HttpClientConnectionManager httpClientConnectionManager;

    @Override
    public void setSettings(Map<String, Object> settings) throws ValidationException{
        for (Map.Entry<String, Object> setting: settings.entrySet()) {
            switch (setting.getKey()){
                case "host":
                    this.host = (String) setting.getValue();
                    break;
                case "port":
                    this.port = (Integer) setting.getValue();
                    break;
                case "ttl":
                    this.ttl = (Long) setting.getValue();
                    break;
                default:
                    throw new ValidationException(String.format("Unexpected field %s for Blueflood writer", setting.getKey()));
            }
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

//                    if (NumberUtils.isNumeric(values.getValue())) {
//                        String name = KeyUtils.getKeyString(server, run, result, values, getTypeNames(), null);
//                        String value = values.getValue().toString();
//                        long time = result.getEpoch();
//
//                        String line = "{ \"metricName\": \"" + name + "\", \"metricValue\": " + value + ", \"collectionTime\": " + time
//                                + ", \"ttlInSeconds\": " + this.ttl + "},";
//                        body += line;
//                    } else {
//
//                    }

        }

        if (body.length() > 1) {
            body = body.substring(0, body.length() - 1);
        }
        body += "]";

        return body;
    }
}
