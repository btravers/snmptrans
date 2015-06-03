package com.zenika.snmptrans.model;

import com.google.common.collect.ImmutableMap;
import com.zenika.snmptrans.AppConfig;
import com.zenika.snmptrans.model.output.GraphiteWriter;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableList.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class GraphiteWriterTest {

    @Test(expected = ValidationException.class)
    public void hostIsRequired() throws ValidationException {
        try {
            Map<String, Object> settings = new HashMap<>();

            settings.put("port", 123);

            GraphiteWriter writer = new GraphiteWriter();
            writer.setSettings(settings);
        } catch (ValidationException e) {
            assertThat(e).hasMessage("Missing host setting");
            throw e;
        }
    }

    @Test(expected = ValidationException.class)
    public void portIsRequired() throws ValidationException {
        try {
            Map<String, Object> settings = new HashMap<>();

            settings.put("host", "localhost");

            GraphiteWriter writer = new GraphiteWriter();
            writer.setSettings(settings);
        } catch (ValidationException e) {
            assertThat(e).hasMessage("Missing port setting");
            throw e;
        }
    }

    @Test(expected = ValidationException.class)
    public void unexpectedSetting() throws ValidationException {
        try {
            Map<String, Object> settings = new HashMap<>();

            settings.put("host", "localhost");
            settings.put("port", 123);
            settings.put("truc", "truc");

            GraphiteWriter writer = new GraphiteWriter();
            writer.setSettings(settings);
        } catch (ValidationException e) {
            assertThat(e).hasMessage("Unexpected field truc for Graphite writer");
            throw e;
        }
    }
}
