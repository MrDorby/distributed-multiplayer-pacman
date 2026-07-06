package it.unibo.controller.server.network.transport;

import it.unibo.controller.server.GameServerNetworkListener;
import it.unibo.controller.server.network.transport.handler.JoinMatchHandler;
import it.unibo.controller.server.network.transport.handler.MoveCommandHandler;
import it.unibo.controller.server.network.transport.handler.UdpHandshakeHandler;
import it.unibo.controller.shared.network.packets.PacketType;

public class NettyGameNetworkServerFactory {
    private NettyGameNetworkServerFactory() {
    }

    public static NettyGameNetworkServer create(int tcpPort, int udpPort, GameServerNetworkListener listener) {
        GameSessionRegistry sessions = new GameSessionRegistry();
        NettyGameNetworkServer server = new NettyGameNetworkServer(tcpPort, udpPort, sessions);
        server.registerTcpHandler(PacketType.JOIN_MATCH, new JoinMatchHandler(sessions, listener, server));
        server.registerUdpHandler(PacketType.UDP_HANDSHAKE, new UdpHandshakeHandler(sessions));
        server.registerUdpHandler(PacketType.MOVE_COMMAND, new MoveCommandHandler(sessions, listener));
        return server;
    }
}
