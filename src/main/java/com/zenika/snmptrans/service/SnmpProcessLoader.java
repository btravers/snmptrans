package com.zenika.snmptrans.service;

import com.zenika.snmptrans.model.SnmpProcess;

import java.io.IOException;
import java.util.Collection;

public interface SnmpProcessLoader {

    Collection<SnmpProcess> getSnmpProcesses() throws IOException;
    boolean haveChanged();

}
