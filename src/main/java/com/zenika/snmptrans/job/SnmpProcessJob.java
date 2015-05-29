package com.zenika.snmptrans.job;

import com.zenika.snmptrans.model.*;
import com.zenika.snmptrans.snmp.SnmpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SnmpProcessJob implements Runnable, AutoCloseable {

    private SnmpProcess snmpProcess;
    private SnmpClient snmpClient;

    public SnmpProcessJob(SnmpProcess snmpProcess) throws IOException {
        this.snmpProcess = snmpProcess;
        this.snmpClient = new SnmpClient(new StringBuilder()
                .append("udp:")
                .append(snmpProcess.getServer().getHost()).append("/")
                .append(snmpProcess.getServer().getPort()).toString());
    }

    @Override
    public void run() {
        List<String> oids = new ArrayList<>();
        for (QuerySet querySet : snmpProcess.getServer().getQuerySets()) {
            for (Query query : querySet.getQueries()) {
                oids.add(query.getOid());
            }
        }

        List<Result> results = null;
        try {
            snmpClient.start();
            snmpClient.get(oids, snmpProcess.getServer(), snmpProcess.getWriters());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (this.snmpClient != null) {
            this.snmpClient.stop();
        }
    }
}
