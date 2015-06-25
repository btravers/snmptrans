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
    public void doWrite(Map<String, Map<String, Map<String, String>>> results, SnmpProcess snmpProcess, long timestamp) {

        try (Socket socket = this.genericKeyedObjectPool.borrowObject(address);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8), true)) {

            String agent = new StringBuilder()
                    .append(snmpProcess.getServer().getHost().replace('.', '_').replace(" ", ""))
                    .append('_')
                    .append(snmpProcess.getServer().getPort())
                    .toString();

            for (Query query : snmpProcess.getQueries()) {

                String name = new StringBuilder()
                        .append(agent)
                        .append(".")
                        .toString();

                if (query.getResultAlias() == null) {
                    name = new StringBuilder()
                            .append(name)
                            .append(query.getObj().replace(".", "_").replace(" ", ""))
                            .append(".")
                            .toString();
                } else {
                    name = new StringBuilder()
                            .append(query.getResultAlias().replace(".", "_").replace(" ", "_"))
                            .append(".")
                            .toString();
                }

                Map<String, Map<String, String>> queryResults = results.get(query.getObj());

                if (queryResults == null) {
                    continue;
                }

                Map<String, String> nameResults = null;
                if (query.getTypeName() != null) {
                    nameResults = queryResults.get(query.getTypeName());
                }

                for (Attribute attr : query.getAttr()) {
                    Map<String, String> attrResults = queryResults.get(attr.getValue());

                    for (Map.Entry<String, String> attrResult : attrResults.entrySet()) {
                        String mectricName = new StringBuilder()
                                .append(name)
                                .append(".")
                                .toString();

                        if (nameResults != null) {
                            mectricName = new StringBuilder()
                                    .append(mectricName)
                                    .append(nameResults.get(attrResult.getKey()))
                                    .toString();
                        } else {
                            mectricName = new StringBuilder()
                                    .append(mectricName)
                                    .append(attrResult.getKey())
                                    .toString();
                        }

                        if (attr.getAlias() != null) {
                            mectricName = new StringBuilder()
                                    .append(mectricName)
                                    .append(".")
                                    .append(attr.getAlias().replace(".", "_").replace(" ", "_"))
                                    .toString();
                        } else {
                            mectricName = new StringBuilder()
                                    .append(mectricName)
                                    .append(".")
                                    .append(attr.getValue().replace(".", "_").replace(" ", ""))
                                    .toString();
                        }

                        String line = new StringBuilder()
                                .append(this.rootPrefix)
                                .append(".")
                                .append(mectricName)
                                .append(" ")
                                .append(attrResult.getValue())
                                .append(" ")
                                .append(timestamp / 1000)
                                .append("\n")
                                .toString();

                        writer.write(line);
                        logger.info("New entry: " + line);
                    }

                }
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
