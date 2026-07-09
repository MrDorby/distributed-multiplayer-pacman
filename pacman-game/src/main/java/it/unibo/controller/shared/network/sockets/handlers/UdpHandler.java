package it.unibo.controller.shared.network.sockets.handlers;

import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;

import java.net.InetSocketAddress;

public interface UdpHandler {
    void handle(InetSocketAddress sender, NetworkPacket packet);
}
