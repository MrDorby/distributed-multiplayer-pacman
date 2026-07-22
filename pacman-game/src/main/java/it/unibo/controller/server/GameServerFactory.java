package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.network.sockets.NettyGameServerGateway;
import it.unibo.controller.server.network.sockets.handlers.*;
import it.unibo.controller.server.network.sockets.session.GameSessionController;
import it.unibo.controller.server.orchestration.DummyGameServerOrchestrator;
import it.unibo.controller.server.orchestration.GameServerOrchestrator;
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
import java.util.Collection;

public class GameServerFactory {
    private static final String MAP_PATH_FORMAT = "maps/%s.json";

    public static GameServer createWithPersistence(
            String matchId,
            String mapName,
            int tcpPort,
            int udpPort,
            URI backupEndpoint,
            URI resultsEndpoint,
            GameServerOrchestrator orchestrator
    ) {
        GamePersistenceManager persistence = new GamePersistenceManager(
                new HttpGameBackupService(),
                new HttpGameResultsService()
        );
        return assemble(matchId, mapName, tcpPort, udpPort, persistence, orchestrator, false, null);
    }

    public static GameServer createWithDummyExtraServices(
            String matchId,
            String mapName,
            int tcpPort,
            int udpPort
    ) {
        GamePersistenceManager persistence = new GamePersistenceManager(
                new DummyGameBackupService(),
                new DummyGameResultsService()
        );
        GameServerOrchestrator dummyOrchestrator = new DummyGameServerOrchestrator();
        return assemble(matchId, mapName, tcpPort, udpPort, persistence, dummyOrchestrator, false, null);
    }

    private static GameServer assemble(
            String matchId,
            String mapName,
            int tcpPort,
            int udpPort,
            GamePersistenceManager persistence,
            GameServerOrchestrator orchestrator,
            boolean isRecovery,
            Collection<String> previousActivePlayers
    ) {
        String mapPath = MAP_PATH_FORMAT.formatted(mapName);
        GameContext gameContext = GameContextFactory.createFromMap(mapPath, new GameEntityFactoryImpl());
        Game game = new GameImpl(gameContext);
        GameSessionController sessionController = new GameSessionController();
        NettyGameServerGateway gateway = new NettyGameServerGateway(tcpPort, udpPort, sessionController);
        ServerGameEngine engine = new ServerGameEngine(game);
        GameServer server = new GameServerImpl(
                matchId,
                engine,
                gateway,
                persistence,
                orchestrator,
                isRecovery,
                previousActivePlayers
        );
        engine.addListener(server);
        sessionController.addListener(server);
        HandlerContext context = new HandlerContext(sessionController, server, gateway);
        gateway.addTcpHandler(PacketType.JOIN_GAME, new JoinGameHandler(context));
        gateway.addUdpHandler(PacketType.UDP_HANDSHAKE, new UdpHandshakeHandler(context));
        gateway.addUdpHandler(PacketType.PACMAN_MOVE_COMMAND, new MoveCommandHandler(context));
        gateway.addTcpHandler(PacketType.EXPLICIT_DISCONNECT, new ExplicitDisconnectHandler(context));
        return server;
    }
}