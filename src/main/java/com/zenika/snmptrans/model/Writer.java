package com.zenika.snmptrans.model;

import java.util.Map;

public interface Writer {

    void setSettings(Map<String, Object> settings) throws ValidationException;

    void doWrite(Map<String, Map<String, Map<String, String>>> results, SnmpProcess snmpProcess, long timestamp);

}
