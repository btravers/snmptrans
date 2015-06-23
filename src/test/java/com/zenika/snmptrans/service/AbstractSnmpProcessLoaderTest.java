package com.zenika.snmptrans.service;

import com.zenika.snmptrans.model.SnmpProcess;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;

public abstract class AbstractSnmpProcessLoaderTest {

    @Autowired
    protected SnmpProcessLoader snmpProcessLoader;

    /**
     * Load some data in the repository:
     *  - /test/document1.json
     *  - /test/document2.json
     */
    @Before
    public abstract void setUp();

    /**
     * Clean all data.
     */
    @After
    public abstract void tearDown();

    public abstract void performChanges() throws IOException;

    @Test
    public void shoudLoadSnmpProcesses() throws IOException {
        Collection<SnmpProcess> list = this.snmpProcessLoader.getSnmpProcesses();
        Assertions.assertThat(list.size()).isEqualTo(2);
    }

    @Test
    public void shouldHaveSomeChanges() throws IOException {
        Assertions.assertThat(this.snmpProcessLoader.haveChanged()).isFalse();

        this.performChanges();

        Assertions.assertThat(this.snmpProcessLoader.haveChanged()).isTrue();
    }

}
