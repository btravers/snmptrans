package com.zenika.snmptrans.snmp;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class SnmpClientTest {

    protected static final String sysDescr = "1.3.6.1.2.1.1.1.0";

    SnmpClient snmpClient = null;

    @Test
    public void getAnSnmpVariable() throws IOException, InterruptedException {
        List<String> oids = new ArrayList<>();
        oids.add(sysDescr);

        Map<String, String> results = snmpClient.get(oids);

        assertThat(results).containsKey(sysDescr);
        assertThat(results.get(sysDescr)).isNotNull();
    }
}
