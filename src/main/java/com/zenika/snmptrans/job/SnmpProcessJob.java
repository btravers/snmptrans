package com.zenika.snmptrans.job;

import com.zenika.snmptrans.exception.LifecycleException;
import com.zenika.snmptrans.model.*;
import com.zenika.snmptrans.snmp.SnmpClient;
import com.zenika.snmptrans.snmp.SnmpV1Client;
import com.zenika.snmptrans.snmp.SnmpV2cClient;
import com.zenika.snmptrans.snmp.SnmpV3Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SnmpProcessJob implements Runnable, AutoCloseable {

    private SnmpProcess snmpProcess;
    private SnmpClient snmpClient;

    public SnmpProcessJob(SnmpProcess snmpProcess) throws IOException, LifecycleException {
        this.snmpProcess = snmpProcess;
        String address = new StringBuilder()
                .append("udp:")
                .append(snmpProcess.getServer().getHost()).append("/")
                .append(snmpProcess.getServer().getPort()).toString();

        switch (this.snmpProcess.getServer().getSnmpVersion()) {
            case V1:
                this.snmpClient = new SnmpV1Client(address,
                        this.snmpProcess.getServer().getCommunity());
                break;
            case V2c:
                this.snmpClient = new SnmpV2cClient(address,
                        this.snmpProcess.getServer().getCommunity());
                break;
            case V3:
                this.snmpClient = new SnmpV3Client(address,
                        this.snmpProcess.getServer().getUserName(),
                        this.snmpProcess.getServer().getSecurityName(),
                        this.snmpProcess.getServer().getAuthenticationPassphrase(),
                        this.snmpProcess.getServer().getPrivacyPassphrase());
                break;
            default:
                throw new LifecycleException("Unexpected SNMP version");
        }

    }

    @Override
    public void run() {
        List<String> oids = new ArrayList<>();
        for (QuerySet querySet : snmpProcess.getQuerySets()) {
            for (Query query : querySet.getQueries()) {
                oids.add(query.getOid());
            }
        }

        try {
            if (!snmpClient.isStarted()) {
                snmpClient.start();
            }
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
