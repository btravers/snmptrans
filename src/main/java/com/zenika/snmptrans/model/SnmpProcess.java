package com.zenika.snmptrans.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class SnmpProcess {

    private Collection<OutputWriter> writers;
    private Server server;

    public Collection<OutputWriter> getWriters() {
        return writers;
    }

    public void setWriters(Collection<Map<String, Object>> writers) throws ValidationException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.writers = new ArrayList<>();

        for (Map<String, Object> writer : writers) {
            String writerClass = (String) writer.get("@class");
            if (writerClass == null) {
                throw new ValidationException("Malformed writer exception");
            }

            Map<String, Object> settings = (Map<String, Object>) writer.get("settings");
            if (settings == null) {
                throw new ValidationException("Malformed writer exception");
            }

            OutputWriter outputWriter = (OutputWriter) Class.forName(writerClass).newInstance();
            outputWriter.setSettings(settings);
            this.writers.add(outputWriter);
        }

    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
