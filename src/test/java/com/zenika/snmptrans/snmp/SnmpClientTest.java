package com.zenika.snmptrans.snmp;

import com.zenika.snmptrans.model.SnmpProcess;
import com.zenika.snmptrans.model.Writer;
import com.zenika.snmptrans.model.Result;
import com.zenika.snmptrans.model.Server;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public abstract class SnmpClientTest {

    protected static final String sysDescr = "1.3.6.1.2.1.1.1.0";

    SnmpClient snmpClient = null;
    SnmpProcess snmpProcess = null;

    @Test
    public void getAnSnmpVariable() throws IOException, InterruptedException {
//        List<String> oids = new ArrayList<>();
//        oids.add(sysDescr);
//
//        Writer writer = mock(Writer.class);
//        List<Writer> writers = new ArrayList<>();
//        writers.add(writer);
//
//        snmpClient.get(oids, snmpProcess, writers);
//
//        ArgumentCaptor<Collection> argumentCaptor = ArgumentCaptor.forClass(Collection.class);
//
//        verify(writer).doWrite(argumentCaptor.capture());
//        assertThat(argumentCaptor.getValue().size()).isEqualTo(1);
//        assertThat(((Result) argumentCaptor.getValue().iterator().next()).getAttr()).isEqualTo(sysDescr);
    }
}
