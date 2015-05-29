package com.zenika.snmptrans;

import com.zenika.snmptrans.job.SnmpProcessJob;
import com.zenika.snmptrans.model.SnmpProcess;
import com.zenika.snmptrans.service.SnmpProcessLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
public class SnmpTransformer {

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

        while (true) {
            if (snmpProcessLoader.haveChanged()) {
                deleteAllJobs();
                startupSystem();
            }

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void startupSystem() {
        try {
            this.snmpProcesses = snmpProcessLoader.getSnmpProcesses();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (SnmpProcess snmpProcess : snmpProcesses) {
            try(SnmpProcessJob job = new SnmpProcessJob(snmpProcess)) {
                threadPoolTaskScheduler.scheduleAtFixedRate(job, 60000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteAllJobs() {
        threadPoolTaskScheduler.shutdown();
    }
}
