package com.zenika.snmptrans.job;

import com.zenika.snmptrans.exception.LifecycleException;
import com.zenika.snmptrans.model.*;
import com.zenika.snmptrans.snmp.SnmpClient;
import com.zenika.snmptrans.snmp.SnmpV1Client;
import com.zenika.snmptrans.snmp.SnmpV2cClient;
import com.zenika.snmptrans.snmp.SnmpV3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnmpProcessJob implements Runnable, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SnmpProcessJob.class);

    private SnmpProcess snmpProcess;
    private SnmpClient snmpClient;
    private Map<String, OIDInfo> oidInfoMap;

    public SnmpProcessJob(SnmpProcess snmpProcess) throws IOException, LifecycleException {
        this.snmpProcess = snmpProcess;

        String address = new StringBuilder()
                .append("udp:")
                .append(this.snmpProcess.getServer().getHost())
                .append("/")
                .append(this.snmpProcess.getServer().getPort())
                .toString();

        switch (this.snmpProcess.getServer().getVersion()) {
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

        this.oidInfoMap = new HashMap<>();

        this.snmpClient.start();

        String agent = new StringBuilder()
                .append(this.snmpProcess.getServer().getHost().replace('.', '_'))
                .append('_')
                .append(this.snmpProcess.getServer().getPort())
                .toString();

        for (Query query : this.snmpProcess.getQueries()) {
            Map<String, String> names = this.snmpClient.snmpWalk(new StringBuilder()
                    .append(query.getObj())
                    .append(".")
                    .append(query.getTypeName())
                    .toString());

            for (Map.Entry<String, String> entry : names.entrySet()) {
                String[] splitted = entry.getKey().split("\\.");
                String id = splitted[splitted.length - 1];

                for (String attr : query.getAttr()) {
                    OIDInfo oidInfo = new OIDInfo();
                    if (query.getResultAlias() == null || query.getResultAlias().equals("")) {
                        oidInfo.setAlias(query.getObj().replace('.', '_'));
                    } else {
                        oidInfo.setAlias(query.getResultAlias().replace('.', '_'));
                    }
                    oidInfo.setName(entry.getValue());
                    oidInfo.setAttr(attr);
                    oidInfo.setAgent(agent);

                    this.oidInfoMap.put(new StringBuilder()
                            .append(query.getObj())
                            .append(".")
                            .append(attr)
                            .append(".")
                            .append(id)
                            .toString(), oidInfo);
                }
            }
        }

        this.snmpClient.stop();
    }

    @Override
    public void run() {
        try {
            this.snmpClient.start();

            List<String> oids = new ArrayList<>();
            oids.addAll(this.oidInfoMap.keySet());
            Map<String, String> results = this.snmpClient.get(oids);
            this.snmpClient.stop();

            for (Writer writer : this.snmpProcess.getWriters()) {
                writer.doWrite(results, this.oidInfoMap, System.currentTimeMillis());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        if (this.snmpClient != null) {
            this.snmpClient.stop();
        }
    }
}
