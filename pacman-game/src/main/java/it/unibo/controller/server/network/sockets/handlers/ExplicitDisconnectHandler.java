package it.unibo.controller.server.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplicitDisconnectHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExplicitDisconnectHandler.class);
    private final HandlerContext ctx;

    public ExplicitDisconnectHandler(HandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        GameSession session = ctx.sessions().getSessionByChannel(channel);
        ctx.sessions().onDisconnect(session);
        logger.debug("Player {} {} has disconnected cleanly.", session.getUsername(), channel.remoteAddress());
    }
}
