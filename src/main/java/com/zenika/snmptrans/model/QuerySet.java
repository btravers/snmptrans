package com.zenika.snmptrans.model;

import java.util.Collection;

public class QuerySet {

    private String description;
    private Collection<Query> queries;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Query> getQueries() {
        return queries;
    }

    public void setQueries(Collection<Query> queries) {
        this.queries = queries;
    }
}
