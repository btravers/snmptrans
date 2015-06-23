package com.zenika.snmptrans.service;

import com.zenika.snmptrans.TestConfig;
import com.zenika.snmptrans.repository.SnmpProcessRepositoryImpl;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ElasticSearchSnmpProcessLoaderTest extends AbstractSnmpProcessLoaderTest {

    @Autowired
    private Client client;

    @Override
    public void setUp() {
        try {
            this.client.prepareIndex(SnmpProcessRepositoryImpl.INDEX, SnmpProcessRepositoryImpl.TYPE)
                    .setRefresh(true)
                    .setSource(IOUtils.toString(getClass().getResourceAsStream("/document1.json")))
                    .execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.client.prepareIndex(SnmpProcessRepositoryImpl.INDEX, SnmpProcessRepositoryImpl.TYPE)
                    .setRefresh(true)
                    .setSource(IOUtils.toString(getClass().getResourceAsStream("/document2.json")))
                    .execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.snmpProcessLoader.haveChanged();
    }

    @Override
    public void tearDown() {
        this.client.prepareDeleteByQuery(SnmpProcessRepositoryImpl.INDEX)
                .setTypes(SnmpProcessRepositoryImpl.TYPE)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();
    }

    @Override
    public void performChanges() throws IOException {
        this.client.prepareIndex(SnmpProcessRepositoryImpl.INDEX, SnmpProcessRepositoryImpl.TYPE)
                .setRefresh(true)
                .setSource(IOUtils.toString(getClass().getResourceAsStream("/document3.json")))
                .execute().actionGet();
    }
}
