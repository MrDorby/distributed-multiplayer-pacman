package it.unibo.controller.server.network.sockets;

import it.unibo.controller.server.GameServerController;
import it.unibo.controller.server.network.sockets.handlers.HandlerContext;
import it.unibo.controller.server.network.sockets.session.GameSessionController;
import it.unibo.controller.server.network.sockets.session.GameSessionRegistry;
import it.unibo.controller.server.network.sockets.handlers.JoinGameHandler;
import it.unibo.controller.server.network.sockets.handlers.MoveCommandHandler;
import it.unibo.controller.server.network.sockets.handlers.UdpHandshakeHandler;
import it.unibo.controller.shared.network.sockets.packets.PacketType;

public class GameNetworkServerFactory {
    private GameNetworkServerFactory() {}

    public static GameNetworkServer create(int tcpPort, int udpPort, GameServerController serverController) {
        GameSessionRegistry sessionRegistry = new GameSessionRegistry();
        GameSessionController sessionController = new GameSessionController(sessionRegistry);
        sessionController.addListener(serverController);
        GameNetworkServer server = new NettyGameNetworkServer(tcpPort, udpPort, sessionRegistry);
        HandlerContext context = new HandlerContext(sessionController, serverController, server);
        server.addTcpHandler(PacketType.JOIN_GAME, new JoinGameHandler(context));
        server.addUdpHandler(PacketType.UDP_HANDSHAKE, new UdpHandshakeHandler(context));
        server.addUdpHandler(PacketType.PACMAN_MOVE_COMMAND, new MoveCommandHandler(context));
        return server;
    }
}
