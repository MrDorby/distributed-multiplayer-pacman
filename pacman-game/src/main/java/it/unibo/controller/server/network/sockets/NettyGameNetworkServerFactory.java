package it.unibo.controller.server.network.transport;

import it.unibo.controller.server.GameServerNetworkListener;
import it.unibo.controller.server.network.transport.handler.JoinGameHandler;
import it.unibo.controller.server.network.transport.handler.MoveCommandHandler;
import it.unibo.controller.server.network.transport.handler.UdpHandshakeHandler;
import it.unibo.controller.shared.network.packets.PacketType;

public class NettyGameNetworkServerFactory {
    private NettyGameNetworkServerFactory() {
    }

    public static NettyGameNetworkServer create(int tcpPort, int udpPort, GameServerNetworkListener listener) {
        GameSessionRegistry sessions = new GameSessionRegistry();
        NettyGameNetworkServer server = new NettyGameNetworkServer(tcpPort, udpPort, sessions);
        server.addTcpHandler(PacketType.JOIN_GAME, new JoinGameHandler(sessions, listener, server));
        server.addUdpHandler(PacketType.UDP_HANDSHAKE, new UdpHandshakeHandler(sessions));
        server.addUdpHandler(PacketType.PACMAN_MOVE_COMMAND, new MoveCommandHandler(sessions, listener));
        return server;
    }
}
