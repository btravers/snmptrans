package com.zenika.snmptrans;

import com.zenika.snmptrans.job.SnmpProcessJob;
import com.zenika.snmptrans.model.SnmpProcess;
import com.zenika.snmptrans.service.SnmpProcessLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
public class SnmpTransformer implements CommandLineRunner {

    private Collection<SnmpProcess> snmpProcesses =  Collections.EMPTY_LIST;

    @Autowired
    private SnmpProcessLoader snmpProcessLoader;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(AppConfig.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
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
            SnmpProcessJob job = new SnmpProcessJob(snmpProcess);
            threadPoolTaskScheduler.scheduleAtFixedRate(job, 60000);
        }
    }

    private void deleteAllJobs() {
        threadPoolTaskScheduler.shutdown();
    }
}
