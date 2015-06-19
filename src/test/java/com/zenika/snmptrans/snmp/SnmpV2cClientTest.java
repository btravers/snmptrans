package com.zenika.snmptrans.snmp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.snmptrans.model.SnmpProcess;
import org.junit.After;
import org.junit.Before;
import org.snmp4j.smi.OID;

import java.io.IOException;

public class SnmpV2cClientTest extends SnmpClientTest {

    SnmpAgent agent;

    @Before
    public void setup() throws IOException {
        this.agent = new SnmpAgent("udp:127.0.0.1/2001");
        this.agent.start();
        this.agent.unregisterManagedObject(agent.getSnmpv2MIB());
        this.agent.registerManagedObject(MOCreator.createReadOnly(new OID(sysDescr), "This Description is set By ShivaSoft"));

        this.snmpClient = new SnmpV2cClient("udp:127.0.0.1/2001", "public");
        this.snmpClient.start();

        ObjectMapper objectMapper = new ObjectMapper();
        //this.snmpProcess = objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("conf.json"), SnmpProcess.class);
    }

    @After
    public void tearDown() throws IOException {
        this.agent.stop();
        this.snmpClient.stop();
    }
}
