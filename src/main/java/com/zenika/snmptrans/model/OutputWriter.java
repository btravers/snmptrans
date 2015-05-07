package com.zenika.snmptrans.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public interface OutputWriter {

    void setSettings(Map<String, Object> settings) throws ValidationException;

    void doWrite(Server server, Collection<Result> results) throws IOException;

}
