package com.zenika.snmptrans.snmp;

import com.zenika.snmptrans.model.Result;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
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

    public Collection<Result> get(Collection<String> oids) throws IOException {
        PDU pdu = new PDU();

        for (String oid : oids) {
            pdu.add(new VariableBinding(new OID(oid)));
        }

        pdu.setType(PDU.GET);
        ResponseEvent event = this.snmp.send(pdu, getTarget(), null);

        if (event != null) {
            Collection<Result> result = Collections.emptyList();

            for (Object response : event.getResponse().getVariableBindings()) {
                Result tmp = new Result();
                tmp.setOid(((VariableBinding) response).getOid().toString());
                tmp.setValue(((VariableBinding) response).getVariable().toString());

                result.add(tmp);
            }

            return result;
        }

        throw new RuntimeException("GET timed out");
    }

    private Target getTarget() {
        Address address = GenericAddress.parse(this.address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(address);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);

        return target;
    }
}
