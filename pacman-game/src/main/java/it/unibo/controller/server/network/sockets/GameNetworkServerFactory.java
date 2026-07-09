package it.unibo.controller.server.network.sockets;

import it.unibo.controller.server.GameServerController;
import it.unibo.controller.shared.network.sockets.handlers.JoinGameHandler;
import it.unibo.controller.shared.network.sockets.handlers.MoveCommandHandler;
import it.unibo.controller.shared.network.sockets.handlers.UdpHandshakeHandler;
import it.unibo.controller.shared.network.sockets.packets.PacketType;

public class GameNetworkServerFactory {
    private GameNetworkServerFactory() {
    }

    public static GameNetworkServer create(int tcpPort, int udpPort, GameServerController controller) {
        GameSessionRegistry sessions = new GameSessionRegistry();
        GameNetworkServer server = new NettyGameNetworkServer(tcpPort, udpPort, sessions);
        server.addTcpHandler(PacketType.JOIN_GAME, new JoinGameHandler(controller, server, sessions));
        server.addUdpHandler(PacketType.UDP_HANDSHAKE, new UdpHandshakeHandler(sessions));
        server.addUdpHandler(PacketType.PACMAN_MOVE_COMMAND, new MoveCommandHandler(sessions, controller));
        return server;
    }
}
