package it.unibo.controller.client;

import it.unibo.controller.client.engine.ClientGameEngine;
import it.unibo.controller.client.network.sockets.GameClientGateway;
import it.unibo.controller.client.network.sockets.NettyGameClientGateway;
import it.unibo.controller.client.network.sockets.handlers.*;
import it.unibo.controller.client.network.sockets.session.ClientGameSessionManager;
import it.unibo.controller.client.network.sockets.session.ClientGameSessionManagerImpl;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameView;

public class GameClientFactory {
    public static GameClient create(
            String host,
            int tcpPort,
            int udpPort,
            String username,
            GameView view
    ) {
        Game game = new GameImpl(null);
        ClientGameEngine engine = new ClientGameEngine(game);
        engine.setView(view);

        GameClientGateway gateway = new NettyGameClientGateway(host, tcpPort, udpPort);
        ClientGameSessionManager sessionManager = new ClientGameSessionManagerImpl(gateway, username);
        gateway.setSessionManager(sessionManager);
        GameClientImpl client = new GameClientImpl(engine, gateway, sessionManager);
        engine.addListener(client);
        sessionManager.addListener(client);
        gateway.addTcpHandler(PacketType.GAME_CONTEXT, new GameContextHandler(client));
        gateway.addTcpHandler(PacketType.GAME_START, new GameStartHandler(client));
        gateway.addTcpHandler(PacketType.JOIN_GAME_ACK, new JoinAckHandler(sessionManager));
        gateway.addUdpHandler(PacketType.GAME_CONTEXT, new GameContextHandler(client));
        gateway.addTcpHandler(PacketType.GAME_ENDED, new GameEndHandler(client));
        gateway.addTcpHandler(PacketType.UDP_HANDSHAKE_ACK, new UdpReadyHandler(sessionManager));
        return client;
    }
}
