package com.zenika.snmptrans.snmp;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.*;

public abstract class SnmpClient {

    private static final int MAX_REPETITIONS = 50;
    private static final int MAX_SIZE_RESPONSE_PDU = 65535;

    Snmp snmp = null;
    String address;
    boolean started = false;

    public SnmpClient(String address) {
        this.address = address;
    }

    public boolean isStarted() {
        return started;
    }

    public void start() throws IOException {
        this.snmp = new Snmp(new DefaultUdpTransportMapping());
        this.snmp.listen();

        this.started = true;
    }

    public void stop() throws IOException {
        if (this.snmp != null) {
            this.snmp.close();
        }
    }

    public Map<String, String> snmpWalk(String oid) throws IOException {
        Map<String, String> varBindings = new HashMap<>();

        String tmp = oid;

        while (tmp.startsWith(oid)) {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GETBULK);

            pdu.setMaxRepetitions(MAX_REPETITIONS);

            Target target = getTarget();
            target.setMaxSizeRequestPDU(MAX_SIZE_RESPONSE_PDU);

            ResponseEvent responseEvent = snmp.send(pdu, target, null);

            if (responseEvent == null) {
                return varBindings;
            }

            PDU response = responseEvent.getResponse();

            if (response == null || response.size() == 0) {
                return varBindings;
            }

            for (int i = 0; i < response.size(); i++) {
                VariableBinding variableBinding = response.get(i);

                if (variableBinding == null) {
                    return varBindings;
                }

                tmp = variableBinding.getOid().toString();
                if (tmp.startsWith(oid)) {
                    varBindings.put(variableBinding.getOid().toString(), variableBinding.getVariable().toString());
                } else {
                    return varBindings;
                }
            }
        }

        return varBindings;
    }

    public Map<String, String> get(Collection<String> oids) throws IOException {
        Map<String, String> varBindings = new HashMap<>();

        PDU pdu = new PDU();
        for (String oid : oids) {
            pdu.add(new VariableBinding(new OID(oid)));
        }
        pdu.setType(PDU.GET);

        Target target = this.getTarget();

        ResponseEvent responseEvent = this.snmp.send(pdu, target, null);

        if (responseEvent == null) {
            return varBindings;
        }

        PDU response = responseEvent.getResponse();

        if (response == null || response.size() == 0) {
            return varBindings;
        }

        for (int i = 0; i < response.size(); i++) {
            VariableBinding variableBinding = response.get(i);

            if (variableBinding == null) {
                return varBindings;
            }

            varBindings.put(variableBinding.getOid().toString(), variableBinding.getVariable().toString());
        }

        return varBindings;
    }

    protected abstract Target getTarget();
}
