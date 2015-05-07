package com.zenika.snmptrans.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zenika.snmptrans.snmp.SnmpClient;

import java.util.Collection;

public class SnmpProcess {

    private Collection<OutputWriter> writers;
    private Server server;
    private SnmpClient snmpClient;

    public Collection<OutputWriter> getWriters() {
        return writers;
    }

    public void setWriters(Collection<OutputWriter> writers) {
        this.writers = writers;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @JsonIgnore
    public SnmpClient getSnmpClient() {
        return snmpClient;
    }

    public void setSnmpClient(SnmpClient snmpClient) {
        this.snmpClient = snmpClient;
    }
}
