package com.zenika.snmptrans.connection;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketFactory extends BaseKeyedPoolableObjectFactory<InetSocketAddress, Socket> {
    private static final Logger logger = LoggerFactory.getLogger(SocketFactory.class);

    @Override
    public Socket makeObject(InetSocketAddress key) throws Exception {
        Socket socket = new Socket(key.getHostName(), key.getPort());
        socket.setKeepAlive(true);
        return socket;
    }

    @Override
    public void destroyObject(InetSocketAddress address, Socket socket) throws Exception {
        socket.close();
    }

    @Override
    public boolean validateObject(InetSocketAddress address, Socket socket) {
        if (socket == null) {
            logger.error("Socket is null [{}]", address);
            return false;
        }

        if (!socket.isBound()) {
            logger.error("Socket is not bound [{}]", address);
            return false;
        }
        if (socket.isClosed()) {
            logger.error("Socket is closed [{}]", address);
            return false;
        }
        if (!socket.isConnected()) {
            logger.error("Socket is not connected [{}]", address);
            return false;
        }
        if (socket.isInputShutdown()) {
            logger.error("Socket input is shutdown [{}]", address);
            return false;
        }
        if (socket.isOutputShutdown()) {
            logger.error("Socket output is shutdown [{}]", address);
            return false;
        }
        return true;
    }
}
