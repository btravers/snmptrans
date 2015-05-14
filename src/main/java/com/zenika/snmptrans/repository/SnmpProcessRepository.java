package com.zenika.snmptrans.repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.zenika.snmptrans.model.SnmpProcess;

import java.io.IOException;
import java.util.Collection;

public interface SnmpProcessRepository {

    Collection<SnmpProcess> getAll() throws IOException;
    boolean haveChanged();

}
