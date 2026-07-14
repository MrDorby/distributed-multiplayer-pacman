package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.network.sockets.NettyGameServerGateway;
import it.unibo.controller.server.network.sockets.handlers.HandlerContext;
import it.unibo.controller.server.network.sockets.handlers.JoinGameHandler;
import it.unibo.controller.server.network.sockets.handlers.MoveCommandHandler;
import it.unibo.controller.server.network.sockets.handlers.UdpHandshakeHandler;
import it.unibo.controller.server.network.sockets.session.GameSessionController;
import it.unibo.controller.server.persistence.GamePersistenceManager;
import it.unibo.controller.server.persistence.backup.DummyGameBackupService;
import it.unibo.controller.server.persistence.backup.HttpGameBackupService;
import it.unibo.controller.server.persistence.results.DummyGameResultsService;
import it.unibo.controller.server.persistence.results.HttpGameResultsService;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;

import java.net.URI;
import java.net.http.HttpClient;

public class GameServerFactory {
    private static final String MAP_PATH_FORMAT = "maps/%s.json";

    public static GameServer createWithPersistence(
            String mapName,
            int tcpPort,
            int udpPort,
            URI backupEndpoint,
            URI resultsEndpoint
    ) {
        HttpClient httpClient = HttpClient.newHttpClient();
        GamePersistenceManager persistence = new GamePersistenceManager(
                new HttpGameBackupService(httpClient, backupEndpoint),
                new HttpGameResultsService(httpClient, resultsEndpoint)
        );
        return assemble(mapName, tcpPort, udpPort, persistence);
    }

    public static GameServer createWithoutPersistence(String mapName, int tcpPort, int udpPort) {
        GamePersistenceManager persistence = new GamePersistenceManager(
                new DummyGameBackupService(),
                new DummyGameResultsService()
        );
        return assemble(mapName, tcpPort, udpPort, persistence);
    }

    private static GameServer assemble(String mapName, int tcpPort, int udpPort, GamePersistenceManager persistence) {
        String mapPath = MAP_PATH_FORMAT.formatted(mapName);
        GameContext gameContext = GameContextFactory.createFromMap(mapPath, new GameEntityFactoryImpl());
        Game game = new GameImpl(gameContext);

        GameSessionController sessionController = new GameSessionController();
        NettyGameServerGateway networkServer = new NettyGameServerGateway(tcpPort, udpPort, sessionController);

        ServerGameEngine engine = new ServerGameEngine(game);

        GameServerImpl server = new GameServerImpl(engine, networkServer, persistence);
        engine.addListener(server);

        sessionController.addListener(server);
        HandlerContext context = new HandlerContext(sessionController, server, networkServer);
        networkServer.addTcpHandler(PacketType.JOIN_GAME, new JoinGameHandler(context));
        networkServer.addUdpHandler(PacketType.UDP_HANDSHAKE, new UdpHandshakeHandler(context));
        networkServer.addUdpHandler(PacketType.PACMAN_MOVE_COMMAND, new MoveCommandHandler(context));
        return server;
    }
}