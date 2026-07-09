package it.unibo.controller.shared.network.sockets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public interface TcpPacketHandler {
    void handle(Channel channel, ByteBufInputStream payload) throws IOException;
}