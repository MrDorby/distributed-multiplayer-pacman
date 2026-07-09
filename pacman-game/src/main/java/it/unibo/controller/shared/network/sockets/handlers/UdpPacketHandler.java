package it.unibo.controller.shared.network.sockets;

import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface UdpPacketHandler {
    void handle(InetSocketAddress sender, ByteBufInputStream payload) throws IOException;
}