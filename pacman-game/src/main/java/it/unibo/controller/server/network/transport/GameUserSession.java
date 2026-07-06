package it.unibo.controller.server.network.transport;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class GameUserSession {
    private final String username;
    private final Channel tcpChannel;
    private InetSocketAddress udpAddress;

    public GameUserSession(String username, Channel tcpChannel) {
        this.username = username;
        this.tcpChannel = tcpChannel;
    }

    public String getUsername() {
        return username;
    }

    public Channel getTcpChannel() {
        return tcpChannel;
    }

    public InetSocketAddress getUdpAddress() {
        return udpAddress;
    }

    public void setUdpAddress(InetSocketAddress udpAddress) {
        this.udpAddress = udpAddress;
    }
}
