package com.zenika.snmptrans.repository;

import com.zenika.snmptrans.model.SnmpProcess;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class SnmpProcessRepositoryImpl implements SnmpProcessRepository {

    @Override
    public Collection<SnmpProcess> getAll() {
        return null;
    }

    @Override
    public boolean haveChanged() {
        return false;
    }
}
