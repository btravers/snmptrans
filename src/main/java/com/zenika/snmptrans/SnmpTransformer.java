package com.zenika.snmptrans;

import com.zenika.snmptrans.exception.LifecycleException;
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

    private Thread shutdownHook = new ShutdownHook();
    private volatile boolean isRunning = false;

    private Collection<SnmpProcess> snmpProcesses =  Collections.EMPTY_LIST;

    @Autowired
    private SnmpProcessLoader snmpProcessLoader;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(AppConfig.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        this.start();

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

        // TODO
    }

    private synchronized void start() throws LifecycleException {
        if (isRunning) {
            throw new LifecycleException("Process already started");
        }

        this.startupSystem();

        Runtime.getRuntime().addShutdownHook(shutdownHook);
        isRunning = true;
    }

    private synchronized void stop() throws LifecycleException {
        if (!isRunning) {
            throw new LifecycleException("Process already started");
        }

        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }

        this.stopServices();

        isRunning = false;
    }

    private synchronized void stopServices() {
        this.snmpProcesses = Collections.EMPTY_LIST;
    }

    private void startupSystem() {
        this.snmpProcesses = this.snmpProcessLoader.getSnmpProcesses();

        this.processSnmpProcessesIntoJobs();
    }

    private void processSnmpProcessesIntoJobs() {
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

    protected class ShutdownHook extends Thread {
        public void run() {
            SnmpTransformer.this.stopServices();
        }
    }
}
