package com.zenika.snmptrans.service;

import com.zenika.snmptrans.model.SnmpProcess;
import com.zenika.snmptrans.repository.SnmpProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SnmpProcessLoaderImpl implements SnmpProcessLoader {

    @Autowired
    private SnmpProcessRepository snmpProcessRepository;

    @Override
    public Collection<SnmpProcess> getSnmpProcesses() {
        return this.snmpProcessRepository.getAll();
    }

    @Override
    public boolean haveChanged() {
        return this.snmpProcessRepository.haveChanged();
    }

}
