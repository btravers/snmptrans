package com.zenika.snmptrans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.zenika.snmptrans.service", "com.zenika.snmptrans.repository"})
public class AppConfig {

    @Value("${continue.on.json.error:false}")
    private boolean continueOnJsonError;

    @Value("${run.endlessly:false}")
    private boolean runEndlessly;

    @Value("${elasticsearch:}")
    private String elasticsearch;

    @Value("${run.period:60}")
    private int runPeriod;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public HttpClientConnectionManager httpClientConnectionManager() {
        return new PoolingHttpClientConnectionManager();
    }

    @Bean
    public Client client() {
        TransportClient client = new TransportClient();
        if (this.elasticsearch.isEmpty()) {
            client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        } else {
            String[] hostAndPort = this.elasticsearch.split(":");
            client.addTransportAddress(new InetSocketTransportAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
        }

        return client;
    }

}
