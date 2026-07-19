package it.unibo.controller.client.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.client.network.sockets.session.ClientGameSessionManager;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.JoinGameAckPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinAckHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(JoinAckHandler.class);

    private final ClientGameSessionManager sessionController;

    public JoinAckHandler(ClientGameSessionManager sessionController) {
        this.sessionController = sessionController;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        JoinGameAckPacket ackPacket = (JoinGameAckPacket) packet;
        logger.debug("Received {} over TCP from {}", ackPacket.getType(), channel.remoteAddress());
        sessionController.onJoinAck(ackPacket.token());
    }
}
