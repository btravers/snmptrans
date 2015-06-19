package com.zenika.snmptrans.model;

import java.util.Map;

public interface Writer {

    void setSettings(Map<String, Object> settings) throws ValidationException;

    void doWrite(Map<String, String> results, Map<String, OIDInfo> oidInfo, long timestamp);

}
