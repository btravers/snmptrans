package com.zenika.snmptrans.snmp;

import org.snmp4j.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;

public class SnmpV2cClient extends SnmpClient {

    private String community;

    public SnmpV2cClient(String address, String community) {
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
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
