package it.unibo.controller.server.network.transport.handler;

import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface UdpPacketHandler {
    void handle(InetSocketAddress sender, ByteBufInputStream payload) throws IOException;
}