package it.unibo.controller.shared.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.server.GameServerController;
import it.unibo.controller.server.network.sockets.GameSessionRegistry;
import it.unibo.controller.server.network.sockets.GameUserSession;
import it.unibo.controller.server.network.sockets.NettyGameNetworkServer;
import it.unibo.controller.shared.network.sockets.packets.JoinGameAckPacket;
import it.unibo.controller.shared.network.sockets.packets.JoinGamePacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class JoinGameHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(JoinGameHandler.class);
    private final GameSessionRegistry sessions;
    private final GameServerController controller;
    private final NettyGameNetworkServer sender;

    public JoinGameHandler(GameServerController controller, NettyGameNetworkServer sender, GameSessionRegistry sessions) {
        this.controller = controller;
        this.sender = sender;
        this.sessions = sessions;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        JoinGamePacket joinGamePacket = (JoinGamePacket) packet;
        String username = joinGamePacket.username();
        GameUserSession session = sessions.register(username, channel);
        logger.info("Player {} successfully connected via TCP.", username);
        String token = UUID.randomUUID().toString();
        session.setUdpToken(token);
        sender.sendTcp(username, new JoinGameAckPacket(token));
        logger.info("Sent JOIN_ACK with token to {}", username);
        controller.onPlayerJoined(username);
    }
}
