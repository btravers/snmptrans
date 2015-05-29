package com.zenika.snmptrans.snmp;

import com.zenika.snmptrans.model.OutputWriter;
import com.zenika.snmptrans.model.Result;
import com.zenika.snmptrans.model.Server;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SnmpClient {

    Snmp snmp = null;
    String address = null;

    public SnmpClient(String address) {
        this.address = address;
    }

    public void start() throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        this.snmp = new Snmp(transport);
        transport.listen();
    }

    public void stop() throws IOException {
        this.snmp.close();
    }

    public void get(List<String> oids, final Server server, final Collection<OutputWriter> writers) throws IOException {
        List<OID> oidObjects = new ArrayList<>();
        for (String oid : oids) {
            System.out.println(oid);
            oidObjects.add(new OID(oid));
        }

        ResponseListener listener = new ResponseListener() {
            @Override
            public void onResponse(ResponseEvent event) {
                if (event == null) {
                    throw new RuntimeException("GET timed out");
                }

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
                    System.out.println(tmp.getValue());

                    result.add(tmp);
                }

                for (OutputWriter writer : writers) {
                    try {
                        writer.doWrite(server, result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        this.snmp.send(getPDU(oidObjects), getTarget(), null, listener);
    }

    private PDU getPDU(List<OID> oids) {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }

        pdu.setType(PDU.GET);
        return pdu;
    }

    private Target getTarget() {
        Address targetAddress = GenericAddress.parse(this.address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
