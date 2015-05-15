package com.zenika.snmptrans.job;

import com.zenika.snmptrans.model.*;
import com.zenika.snmptrans.snmp.SnmpClient;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class SnmpProcessJob implements Runnable {

    private SnmpProcess snmpProcess;

    public SnmpProcessJob(SnmpProcess snmpProcess) {
        this.snmpProcess = snmpProcess;
    }

    @Override
    public void run() {
        SnmpClient snmpClient = snmpProcess.getSnmpClient();
        if (snmpClient == null) {
            snmpClient = new SnmpClient(new StringBuilder()
                    .append("udp:")
                    .append(snmpProcess.getServer().getHost()).append("/")
                    .append(snmpProcess.getServer().getPort()).toString());
            try {
                snmpClient.start();
                snmpProcess.setSnmpClient(snmpClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Collection<String> oids = Collections.emptyList();
        for (QuerySet querySet : snmpProcess.getServer().getQuerySets()) {
            for (Query query : querySet.getQueries()) {
                oids.add(query.getOid());
            }
        }

        Collection<Result> results = null;
        try {
            results = snmpClient.get(oids);
            for (OutputWriter writer : snmpProcess.getWriters()) {
                try {
                    writer.doWrite(snmpProcess.getServer(), results);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
