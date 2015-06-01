package com.zenika.snmptrans.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;

public class SnmpV1Client extends SnmpClient {

    private String community;

    public SnmpV1Client(String address, String community) {
        super(address);
        this.community = community;
    }

    @Override
    protected Target getTarget() {
        Address targetAddress = GenericAddress.parse(this.address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(this.community));
        target.setAddress(targetAddress);
        target.setTimeout(1500);
        target.setRetries(0);
        target.setVersion(SnmpConstants.version1);
        return target;
    }
}
