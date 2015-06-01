package com.zenika.snmptrans.model.output;

import com.zenika.snmptrans.utils.AppContext;
import com.zenika.snmptrans.model.OutputWriter;
import com.zenika.snmptrans.model.Result;
import com.zenika.snmptrans.model.Server;
import com.zenika.snmptrans.model.ValidationException;
import org.apache.commons.codec.Charsets;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

public class GraphiteWriter implements OutputWriter {

    private static final String DEFAULT_ROOT_PREFIX = "servers";

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
                    throw new ValidationException(String.format("Unexpected field %s for Blueflood writer", setting.getKey()));
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
    public void doWrite(Server server, Collection<Result> results) throws IOException {

        try(Socket socket = this.genericKeyedObjectPool.borrowObject(address);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8), true)) {

            for (Result result : results) {
                String name = new StringBuilder()
                        .append(this.rootPrefix)
                        .append(".")
                        .append(server.getHost().replace(".", "_"))
                        .append("_")
                        .append(server.getPort())
                        .toString();
                long timestamp = result.getTimestamp()/1000;
                Object value = result.getValue();

                String line = new StringBuilder()
                        .append(name)
                        .append(" ")
                        .append(value)
                        .append(" ")
                        .append(timestamp)
                        .append("\n")
                        .toString();
                writer.write(line);
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
