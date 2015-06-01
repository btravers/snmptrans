package com.zenika.snmptrans.model;

public class Server {
    private String description;
    private String host;
    private Integer port;

    private SnmpVersion snmpVersion;

    // For SNMP V1 and V2c
    private String community;

    // Defined for SNMP V3
    private String userName;

    // Defined for SNMP V3
    private String securityName;

    // Defined for SNMP V3
    private String authenticationPassphrase;

    // Defined for SNMP V3
    private String privacyPassphrase;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public SnmpVersion getSnmpVersion() {
        return snmpVersion;
    }

    public void setSnmpVersion(SnmpVersion snmpVersion) {
        this.snmpVersion = snmpVersion;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSecurityName() {
        return securityName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public String getAuthenticationPassphrase() {
        return authenticationPassphrase;
    }

    public void setAuthenticationPassphrase(String authenticationPassphrase) {
        this.authenticationPassphrase = authenticationPassphrase;
    }

    public String getPrivacyPassphrase() {
        return privacyPassphrase;
    }

    public void setPrivacyPassphrase(String privacyPassphrase) {
        this.privacyPassphrase = privacyPassphrase;
    }

    public enum SnmpVersion {
        V1, V2c, V3
    }

}
