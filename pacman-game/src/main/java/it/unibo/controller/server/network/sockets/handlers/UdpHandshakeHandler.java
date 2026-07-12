package it.unibo.controller.server.network.sockets.handlers;

import it.unibo.controller.shared.network.sockets.handlers.UdpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.UdpHandshakePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class UdpHandshakeHandler implements UdpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UdpHandshakeHandler.class);
    private final HandlerContext ctx;

    public UdpHandshakeHandler(HandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(InetSocketAddress sender, NetworkPacket packet) {
        UdpHandshakePacket udpHandshakePacket = (UdpHandshakePacket) packet;
        String token = udpHandshakePacket.token();
        logger.debug("Received {} over UDP from {}", udpHandshakePacket.getType(), sender);
        ctx.sessions().onUdpHandshake(token, sender);
    }
}
