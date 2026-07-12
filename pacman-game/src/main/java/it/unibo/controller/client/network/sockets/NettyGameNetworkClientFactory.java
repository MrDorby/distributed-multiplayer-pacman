package it.unibo.controller.client.network.sockets;

import it.unibo.controller.client.GameClientController;
import it.unibo.controller.client.network.sockets.handlers.GameContextHandler;
import it.unibo.controller.client.network.sockets.handlers.GameStartHandler;
import it.unibo.controller.client.network.sockets.handlers.JoinAckHandler;
import it.unibo.controller.shared.network.sockets.packets.PacketType;

public class NettyGameNetworkClientFactory {

    private NettyGameNetworkClientFactory() {
    }

    public static NettyGameNetworkClient create(String host, int tcpPort, int udpPort, GameClientController listener) {
        NettyGameNetworkClient client = new NettyGameNetworkClient(host, tcpPort, udpPort);
        client.addTcpHandler(PacketType.GAME_CONTEXT, new GameContextHandler(listener));
        client.addTcpHandler(PacketType.GAME_START, new GameStartHandler(listener));
        client.addTcpHandler(PacketType.JOIN_GAME_ACK, new JoinAckHandler(client));
        client.addUdpHandler(PacketType.GAME_CONTEXT, new GameContextHandler(listener));
        return client;
    }
}
