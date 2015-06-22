package com.zenika.snmptrans.service;

import com.zenika.snmptrans.job.SnmpProcessJob;
import com.zenika.snmptrans.model.SnmpProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class ScheduledLoader {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledLoader.class);

    private Collection<SnmpProcess> snmpProcesses = Collections.EMPTY_LIST;

    @Autowired
    private SnmpProcessLoader snmpProcessLoader;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Value("${run.period:60000}")
    private long period;

    @PreDestroy
    public void tearDown() {
        this.threadPoolTaskScheduler.shutdown();
    }


    @Scheduled(fixedRate = 60000)
    public void run() {
        if (this.snmpProcessLoader.haveChanged()) {
            this.deleteAllJobs();
            this.startupSystem();
        }
    }

    private void startupSystem() {
        try {
            this.snmpProcesses = this.snmpProcessLoader.getSnmpProcesses();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        for (SnmpProcess snmpProcess : this.snmpProcesses) {
            try(SnmpProcessJob job = new SnmpProcessJob(snmpProcess)) {
                this.threadPoolTaskScheduler.scheduleAtFixedRate(job, this.period);
            } catch (IOException e) {
                logger.error(e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void deleteAllJobs() {
        if (this.threadPoolTaskScheduler.getThreadGroup() != null) {
            this.threadPoolTaskScheduler.getThreadGroup().destroy();
        }
    }
}
