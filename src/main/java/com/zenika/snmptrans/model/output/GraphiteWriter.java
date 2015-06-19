package com.zenika.snmptrans.model.output;

import com.zenika.snmptrans.model.*;
import com.zenika.snmptrans.utils.AppContext;
import org.apache.commons.codec.Charsets;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

public class GraphiteWriter implements Writer {
    private static final Logger logger = LoggerFactory.getLogger(GraphiteWriter.class);

    private static final String DEFAULT_ROOT_PREFIX = "server";

    private InetSocketAddress address;
    private String rootPrefix;

    private GenericKeyedObjectPool<InetSocketAddress, Socket> genericKeyedObjectPool = AppContext.getApplicationContext().getBean(GenericKeyedObjectPool.class);

    @Override
    public void setSettings(Map<String, Object> settings) throws ValidationException {
        String host = null;
        Integer port = null;
        for (Map.Entry<String, Object> setting : settings.entrySet()) {
            switch (setting.getKey()) {
                case "host":
                    host = (String) setting.getValue();
                    break;
                case "port":
                    port = (Integer) setting.getValue();
                    break;
                case "rootPrefix":
                    this.rootPrefix = (String) setting.getValue();
                    break;
                default:
                    throw new ValidationException(String.format("Unexpected field %s for Graphite writer", setting.getKey()));
            }
        }

        if (host == null) {
            throw new ValidationException("Missing host setting");
        }

        if (port == null) {
            throw new ValidationException("Missing port setting");
        }

        this.address = new InetSocketAddress(host, port);

        if (this.rootPrefix == null) {
            this.rootPrefix = DEFAULT_ROOT_PREFIX;
        }
    }

    @Override
    public void doWrite(Map<String, String> results, Map<String, OIDInfo> oidInfo, long timestamp) {

        try (Socket socket = this.genericKeyedObjectPool.borrowObject(address);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8), true)) {

            for (Map.Entry<String, String> result : results.entrySet()) {
                OIDInfo info = oidInfo.get(result.getKey());

                String line = new StringBuilder()
                        .append(this.rootPrefix)
                        .append(".")
                        .append(info.getAgent())
                        .append(".")
                        .append(info.getAlias())
                        .append(".")
                        .append(info.getName())
                        .append(".")
                        .append(info.getAttr())
                        .append(" ")
                        .append(result.getValue())
                        .append(" ")
                        .append(timestamp / 1000)
                        .append("\n")
                        .toString();

                writer.write(line);
                logger.info("New entry: " + line);
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
