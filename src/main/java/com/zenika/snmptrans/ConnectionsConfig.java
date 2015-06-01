package com.zenika.snmptrans;

import com.zenika.snmptrans.connection.SocketFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Socket;

@Configuration
public class ConnectionsConfig {

    @Value("${elasticsearch:}")
    private String elasticsearch;

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

    @Bean
    public GenericKeyedObjectPool<InetSocketAddress, Socket> genericKeyedObjectPool() {
        GenericKeyedObjectPool<InetSocketAddress, Socket> pool = new GenericKeyedObjectPool<>(new SocketFactory());
        pool.setTestOnBorrow(true);
        pool.setMaxActive(-1);
        pool.setMaxIdle(-1);
        pool.setTimeBetweenEvictionRunsMillis(1000 * 60 * 5);
        pool.setMinEvictableIdleTimeMillis(1000 * 60 * 5);

        return pool;
    }

}
