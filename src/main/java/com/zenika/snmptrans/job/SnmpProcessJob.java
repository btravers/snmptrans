package com.zenika.snmptrans.job;

import com.zenika.snmptrans.model.OutputWriter;
import com.zenika.snmptrans.model.Query;
import com.zenika.snmptrans.model.Result;
import com.zenika.snmptrans.model.SnmpProcess;
import com.zenika.snmptrans.snmp.SnmpClient;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class SnmpProcessJob extends QuartzJobBean {

    private int timeout;

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getMergedJobDataMap();
        SnmpProcess snmpProcess = (SnmpProcess) map.get(SnmpProcess.class.getName());

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
        for (Query query : snmpProcess.getServer().getQueries()) {
            oids.addAll(query.getOids());
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
