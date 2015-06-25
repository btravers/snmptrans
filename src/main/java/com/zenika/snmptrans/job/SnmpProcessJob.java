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
import java.util.HashMap;
import java.util.Map;

public class SnmpProcessJob implements Runnable, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SnmpProcessJob.class);

    private SnmpProcess snmpProcess;
    private SnmpClient snmpClient;

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
    }

    @Override
    public void run() {
        try {
            this.snmpClient.start();

            Map<String, Map<String, Map<String, String>>> results = new HashMap<>();
            for (Query query : this.snmpProcess.getQueries()) {
                try {
                    results.put(query.getObj(), this.snmpClient.snmpWalk(query.getObj()));
                } catch (LifecycleException e) {
                    logger.error(e.getMessage());
                }
            }
            this.snmpClient.stop();

            for (Writer writer : this.snmpProcess.getWriters()) {
                writer.doWrite(results, this.snmpProcess, System.currentTimeMillis());
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
