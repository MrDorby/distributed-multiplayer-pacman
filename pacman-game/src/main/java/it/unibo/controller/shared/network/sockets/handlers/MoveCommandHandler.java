package it.unibo.controller.shared.network.sockets.handlers;

import it.unibo.controller.server.GameServerNetworkListener;
import it.unibo.controller.server.network.sockets.GameSessionRegistry;
import it.unibo.controller.server.network.sockets.GameUserSession;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacmanMovePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class MoveCommandHandler implements UdpHandler {
    private static final Logger logger = LoggerFactory.getLogger(MoveCommandHandler.class);
    private final GameSessionRegistry sessions;
    private final GameServerNetworkListener listener;

    public MoveCommandHandler(GameSessionRegistry sessions, GameServerNetworkListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }

    @Override
    public void handle(InetSocketAddress sender, NetworkPacket packet) {
        PacmanMovePacket movePacket = (PacmanMovePacket) packet;
        String senderId = movePacket.pacmanId();
        GameUserSession session = sessions.get(senderId);
        if (session != null && sender.equals(session.getUdpAddress())) {
            listener.onCommandReceived(senderId, new PacmanMoveCommand(movePacket.pacmanId(), movePacket.direction()));
        } else {
            logger.warn("Intercepted bad UDP packet from: {}", sender);
        }
    }
}