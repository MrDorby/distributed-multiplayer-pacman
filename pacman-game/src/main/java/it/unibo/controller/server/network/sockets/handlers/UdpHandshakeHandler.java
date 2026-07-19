package it.unibo.controller.server.network.sockets.handlers;

import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.server.network.sockets.session.UdpHandshakeResult;
import it.unibo.controller.server.network.sockets.session.GameSessionState;
import it.unibo.controller.shared.network.sockets.handlers.UdpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.UdpHandshakeAckPacket;
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
        UdpHandshakePacket handshake = (UdpHandshakePacket) packet;
        String token = handshake.token();
        GameSession session = ctx.sessions().getSessionByUdpToken(token);
        if (session == null) {
            logger.debug("Rejected UDP handshake from {} because no session was found", sender);
            return;
        }
        GameSessionState previousState = session.getState();
        UdpHandshakeResult result = ctx.sessions().onUdpHandshake(token, sender);
        if (result == UdpHandshakeResult.ACCEPTED) {
            ctx.gateway().sendTcp(session.getUsername(), new UdpHandshakeAckPacket());
            ctx.sessions().onSessionReady(session, previousState);
        } else {
            logger.debug("Rejected UDP handshake from {}: {}", sender, result);
        }
    }
}
