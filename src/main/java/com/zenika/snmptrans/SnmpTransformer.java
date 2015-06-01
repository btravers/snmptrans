package com.zenika.snmptrans;

import com.zenika.snmptrans.job.SnmpProcessJob;
import com.zenika.snmptrans.model.SnmpProcess;
import com.zenika.snmptrans.service.SnmpProcessLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
public class SnmpTransformer {
    private static final Logger logger = LoggerFactory.getLogger(SnmpTransformer.class);

    private Collection<SnmpProcess> snmpProcesses = Collections.EMPTY_LIST;

    private SnmpProcessLoader snmpProcessLoader;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public static void main(String... args) {
        ApplicationContext applicationContext = SpringApplication.run(AppConfig.class, args);

        SnmpTransformer snmpTransformer = new SnmpTransformer();
        snmpTransformer.setSnmpProcessLoader(applicationContext.getBean(SnmpProcessLoader.class));
        snmpTransformer.setThreadPoolTaskScheduler(applicationContext.getBean(ThreadPoolTaskScheduler.class));
        snmpTransformer.run();
    }

    public void setSnmpProcessLoader(SnmpProcessLoader snmpProcessLoader) {
        this.snmpProcessLoader = snmpProcessLoader;
    }

    public void setThreadPoolTaskScheduler(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    public void run() {
        this.startupSystem();
        // Initialize variables
        this.snmpProcessLoader.haveChanged();

        while (true) {
            if (this.snmpProcessLoader.haveChanged()) {
                deleteAllJobs();
                startupSystem();
            }

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                break;
            }
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
                this.threadPoolTaskScheduler.scheduleAtFixedRate(job, 60000);
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
