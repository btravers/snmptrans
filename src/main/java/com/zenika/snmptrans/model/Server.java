package com.zenika.snmptrans.model;

import java.util.Collection;

public class Server {
    private String description;
    private String host;
    private Integer port;
    private Collection<QuerySet> querySets;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Collection<QuerySet> getQuerySets() {
        return querySets;
    }

    public void setQuerySets(Collection<QuerySet> querySets) {
        this.querySets = querySets;
    }

}
