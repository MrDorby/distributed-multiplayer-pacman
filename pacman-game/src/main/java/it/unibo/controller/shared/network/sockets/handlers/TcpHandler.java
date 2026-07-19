package it.unibo.controller.shared.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;

public interface TcpHandler {
    void handle(Channel channel, NetworkPacket packet);
}
