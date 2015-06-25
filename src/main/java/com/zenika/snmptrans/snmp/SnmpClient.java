package com.zenika.snmptrans.snmp;

import com.zenika.snmptrans.exception.LifecycleException;
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

    public Map<String, Map<String, String>> snmpWalk(String oid) throws IOException, LifecycleException {
        Map<String, Map<String, String>> varBindings = new HashMap<>();

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
                    String currentOid = variableBinding.getOid().toString();
                    String diff = currentOid.substring(oid.length()+1, currentOid.length());

                    String[] splitted = diff.split("\\.");

                    if (splitted.length < 2) {
                        throw new LifecycleException("No attribute found");
                    }

                    Map<String, String> varBinding = varBindings.get(splitted[0]);
                    if (varBinding == null) {
                        varBinding = new HashMap<>();
                        varBindings.put(splitted[0], varBinding);
                    }

                    String attr = "";
                    for (int j=1; j<splitted.length; j++) {
                        attr += splitted[j];
                        if (j != splitted.length-1) {
                            attr += ".";
                        }
                    }
                    varBinding.put(attr, variableBinding.getVariable().toString());
                } else {
                    return varBindings;
                }
            }
        }

        return varBindings;
    }

    protected abstract Target getTarget();
}
