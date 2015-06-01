package com.zenika.snmptrans.connection;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;

import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketFactory extends BaseKeyedPoolableObjectFactory<InetSocketAddress, Socket> {

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
//            log.error("Socket is null [{}]", address);
            return false;
        }

        if (!socket.isBound()) {
//            log.error("Socket is not bound [{}]", address);
            return false;
        }
        if (socket.isClosed()) {
//            log.error("Socket is closed [{}]", address);
            return false;
        }
        if (!socket.isConnected()) {
//            log.error("Socket is not connected [{}]", address);
            return false;
        }
        if (socket.isInputShutdown()) {
//            log.error("Socket input is shutdown [{}]", address);
            return false;
        }
        if (socket.isOutputShutdown()) {
//            log.error("Socket output is shutdown [{}]", address);
            return false;
        }
        return true;
    }
}
