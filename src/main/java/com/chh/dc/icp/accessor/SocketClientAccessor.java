package com.chh.dc.icp.accessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClientAccessor extends AbstractAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(SocketClientAccessor.class);

    private String host;

    private int port;

    private int timeoutSec;

    private InetSocketAddress addr;

    private DataPackage data;

    private boolean isAccessed = false;

    private Socket socket;

    /**
     * @param host
     * @param port
     * @param timeoutSec
     */
    public SocketClientAccessor(String host, int port, int timeoutSec) {
        addr = new InetSocketAddress(host, port);
        if (timeoutSec < 1) {
            this.timeoutSec = 60;
        } else {
            this.timeoutSec = timeoutSec;
        }
    }


    @Override
    public boolean access() {
        socket = new Socket();
        try {
            socket.setSoTimeout(timeoutSec * 1000);
            socket.connect(addr, timeoutSec * 1000);
            data = new DataPackage(socket.getInputStream());
            isAccessed = true;
        } catch (IOException e) {
            LOG.warn("Socket client accessor has problem about I/O");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                }
            }
            return false;
        }
        return true;
    }


    @Override
    public DataPackage getData() {
        if (!isAccessed) {
            throw new IllegalAccessError("Accessor had not accessed!");
        }
        return data;
    }


    @Override
    public boolean stop() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e1) {

            }
        }
        isAccessed = false;
        return true;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the timeoutSec
     */
    public int getTimeoutSec() {
        return timeoutSec;
    }

    /**
     * @param timeoutSec the timeoutSec to set
     */
    public void setTimeoutSec(int timeoutSec) {
        this.timeoutSec = timeoutSec;
    }
}
