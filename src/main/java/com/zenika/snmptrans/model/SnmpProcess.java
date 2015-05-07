package com.zenika.snmptrans.model;

import java.util.Collection;

public class SnmpProcess {

    private Collection<OutputWriter> writer;
    private Collection<Server> servers;

    public Collection<OutputWriter> getWriter() {
        return writer;
    }

    public void setWriter(Collection<OutputWriter> writer) {
        this.writer = writer;
    }

    public Collection<Server> getServers() {
        return servers;
    }

    public void setServers(Collection<Server> servers) {
        this.servers = servers;
    }
}
