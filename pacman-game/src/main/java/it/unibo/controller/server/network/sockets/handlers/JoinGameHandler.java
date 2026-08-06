package it.unibo.controller.server.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.JoinGameAckPacket;
import it.unibo.controller.shared.network.sockets.packets.JoinServerPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinGameHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(JoinGameHandler.class);
    private final HandlerContext ctx;

    public JoinGameHandler(HandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        JoinServerPacket joinServerPacket = (JoinServerPacket) packet;
        String username = joinServerPacket.username();
        GameSession session = ctx.sessions().onTcpConnect(username, channel);
        if (session == null) {
            logger.debug("Join sequence aborted for player: {}", username);
            return;
        }
        logger.debug("Player {} successfully connected via TCP.", username);
        String token = session.getUdpToken().value();
        NetworkPacket joinGameAckPacket = new JoinGameAckPacket(token);
        ctx.gateway().sendTcp(username, joinGameAckPacket);
        logger.debug("Sent {} with token to {}", joinGameAckPacket.getType(), username);
    }
}
