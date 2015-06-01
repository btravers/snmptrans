package com.zenika.snmptrans.snmp;

import com.zenika.snmptrans.model.OutputWriter;
import com.zenika.snmptrans.model.Result;
import com.zenika.snmptrans.model.Server;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SnmpClient {

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

    public void get(List<String> oids, final Server server, final Collection<OutputWriter> writers) throws IOException {
        List<OID> oidObjects = new ArrayList<>();
        for (String oid : oids) {
            oidObjects.add(new OID(oid));
        }

        // Build the listener
        ResponseListener listener = new ResponseListener() {
            @Override
            public void onResponse(ResponseEvent event) {
                ((Snmp) event.getSource()).cancel(event.getRequest(), this);

                PDU responsePDU = event.getResponse();
                if (responsePDU == null) {
                    throw new RuntimeException("Error: Response PDU is null");
                }

                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus != PDU.noError) {
                    throw new RuntimeException("Request Failed \nError Status = " + errorStatus + "\nError Index = " + errorIndex + " Error Status Text = " + errorStatusText);
                }

                List<Result> result = new ArrayList<>();

                for (Object response : event.getResponse().getVariableBindings()) {
                    Result tmp = new Result();
                    tmp.setOid(((VariableBinding) response).getOid().toString());
                    tmp.setValue(((VariableBinding) response).getVariable().toString());

                    result.add(tmp);
                }

                for (OutputWriter writer : writers) {
                    writer.doWrite(server, result);
                }
            }
        };

        // Build the request PDU
        PDU pdu = new PDU();
        for (OID oid : oidObjects) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);

        // Build the target
        Target target = this.getTarget();

        // Send the request
        this.snmp.send(pdu, target, null, listener);
    }

    protected abstract Target getTarget();
}
