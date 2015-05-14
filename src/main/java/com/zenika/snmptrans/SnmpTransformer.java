package com.zenika.snmptrans;

import com.zenika.snmptrans.model.SnmpProcess;
import com.zenika.snmptrans.service.SnmpProcessLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
public class SnmpTransformer implements CommandLineRunner {

    private Collection<SnmpProcess> snmpProcesses =  Collections.EMPTY_LIST;

    @Autowired
    private SnmpProcessLoader snmpProcessLoader;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(AppConfig.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        this.startupSystem();

        while (true) {

            if (this.snmpProcessLoader.haveChanged()) {
                this.deleteAllJobs();
                this.startupSystem();
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void startupSystem() {
        this.snmpProcesses = this.snmpProcessLoader.getSnmpProcesses();

        for (SnmpProcess snmpProcess : this.snmpProcesses) {
            this.scheduleJob(snmpProcess);
        }
    }

    private void scheduleJob(SnmpProcess snmpProcess) {
        // TODO
    }

    private void deleteAllJobs() {
        // TODO
    }
}
