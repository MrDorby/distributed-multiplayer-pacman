package it.unibo.controller.shared.network.sockets.handlers;

import it.unibo.controller.server.network.sockets.GameSessionRegistry;
import it.unibo.controller.server.network.sockets.GameUserSession;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.UdpHandshakePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class UdpHandshakeHandler implements UdpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UdpHandshakeHandler.class);
    private final GameSessionRegistry sessions;

    public UdpHandshakeHandler(GameSessionRegistry sessions) {
        this.sessions = sessions;
    }

    @Override
    public void handle(InetSocketAddress sender, NetworkPacket packet) {
        UdpHandshakePacket udpHandshakePacket = (UdpHandshakePacket) packet;
        String token = udpHandshakePacket.token();
        GameUserSession session = sessions.getByUdpToken(token);
        if (session != null) {
            session.setUdpAddress(sender);
            session.setUdpToken(null);
            logger.info("Player {} UDP bound to remote endpoint: {}", session.getUsername(), sender);
        } else {
            logger.warn("Received UDP handshake with an invalid or expired token from endpoint: {}", sender);
        }
    }
}
