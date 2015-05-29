package com.zenika.snmptrans.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.snmptrans.model.SnmpProcess;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Repository
public class SnmpProcessRepositoryImpl implements SnmpProcessRepository {

    public final static String INDEX = ".snmptrans";
    public final static String TYPE = "conf";

    private int size = 0;
    private long timestamps = 0;

    @Autowired
    private Client client;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public Collection<SnmpProcess> getAll() throws IOException {
        SearchResponse response = client.prepareSearch(INDEX).setTypes(TYPE)
                .setSize(Integer.MAX_VALUE)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();

        Collection<SnmpProcess> snmpProcesses = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            snmpProcesses.add(mapper.readValue(hit.getSourceAsString(), SnmpProcess.class));
        }
        return snmpProcesses;
    }

    @Override
    public boolean haveChanged() {
//        SearchResponse response = client.prepareSearch(INDEX).setTypes(TYPE)
//                .setQuery(QueryBuilders.matchAllQuery())
//                .addAggregation(AggregationBuilders.terms("agg").field("_timestamp").size(0).order(Terms.Order.term(false)))
//                .execute().actionGet();
//
//        Terms agg = response.getAggregations().get("agg");
//
//        if (agg.getBuckets().size() == 0) {
//            if (this.size == 0) {
//                return false;
//            }
//
//            this.size = 0;
//            return true;
//        }
//
//        if (agg.getBuckets().size() != this.size) {
//            this.size = agg.getBuckets().size();
//            this.timestamps = Long.parseLong(agg.getBuckets().get(0).getKey());
//            return true;
//        }
//
//        if (this.timestamps < Long.parseLong(agg.getBuckets().get(0).getKey())) {
//            this.timestamps = Long.parseLong(agg.getBuckets().get(0).getKey());
//            return  true;
//        }

        return false;
    }
}
