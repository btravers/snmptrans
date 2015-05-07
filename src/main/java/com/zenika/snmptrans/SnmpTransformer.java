package com.zenika.snmptrans;

import com.zenika.snmptrans.exception.LifecycleException;
import com.zenika.snmptrans.model.Server;
import com.zenika.snmptrans.service.SnmpProcessLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class SnmpTransformer {

    private Thread shutdownHook = new ShutdownHook();
    private volatile boolean isRunning = false;

    private Collection<Server> servers =  Collections.EMPTY_LIST;

    @Autowired
    private SnmpProcessLoader snmpProcessLoader;

    public void mainTask() throws LifecycleException {

        this.start();

        while (true) {

            if (snmpProcessLoader.haveChanged()) {
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
        this.servers = Collections.EMPTY_LIST;
    }



    private void startupSystem() {
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
