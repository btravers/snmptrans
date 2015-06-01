package com.zenika.snmptrans.snmp;

import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;

public class SnmpV3Client extends SnmpClient {

    private String userName;
    private String securityName;
    private String authenticationPassphrase;
    private String privacyPassphrase;

    public SnmpV3Client(String address, String userName, String securityName, String authenticationPassphrase, String privacyPassphrase) {
        super(address);
        this.userName = userName;
        this.securityName = securityName;
        this.authenticationPassphrase = authenticationPassphrase;
        this.privacyPassphrase = privacyPassphrase;

        UsmUser user = new UsmUser(new OctetString(this.securityName),
                AuthSHA.ID,
                new OctetString(this.authenticationPassphrase),
                PrivAES256.ID,
                new OctetString(this.privacyPassphrase));

        byte[] authEngineId =snmp.discoverAuthoritativeEngineID(getTarget().getAddress(), 1500);
        this.snmp.getUSM().addUser(new OctetString(this.userName), new OctetString(authEngineId), user);
    }

    @Override
    protected Target getTarget() {
        UserTarget target = new UserTarget();
        target.setAddress(GenericAddress.parse(this.address));
        target.setSecurityName(new OctetString(this.securityName));
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setTimeout(1500);
        target.setRetries(0);

        return target;
    }
}
