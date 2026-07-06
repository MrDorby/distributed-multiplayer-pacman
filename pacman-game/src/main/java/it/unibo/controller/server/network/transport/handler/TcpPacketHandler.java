package it.unibo.controller.server.network.transport.handler;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public interface TcpPacketHandler {
    void handle(Channel channel, ByteBufInputStream payload) throws IOException;
}