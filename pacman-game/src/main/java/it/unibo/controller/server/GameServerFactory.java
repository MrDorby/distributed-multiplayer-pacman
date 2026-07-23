package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.lobby.MatchLifecycleManager;
import it.unibo.controller.server.lobby.RecoveryMatchLifecycleManager;
import it.unibo.controller.server.lobby.StandardMatchLifecycleManager;
import it.unibo.controller.server.network.sockets.NettyGameServerGateway;
import it.unibo.controller.server.network.sockets.handlers.*;
import it.unibo.controller.server.network.sockets.session.GameSessionController;
import it.unibo.controller.server.orchestration.DummyGameServerOrchestrator;
import it.unibo.controller.server.orchestration.GameServerOrchestrator;
import it.unibo.controller.server.persistence.GamePersistenceManager;
import it.unibo.controller.server.persistence.backup.*;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.results.LocalGameResultsService;
import it.unibo.controller.server.persistence.results.HttpGameResultsService;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;

import java.net.URI;

/**
 * Factory class responsible for assembling and configuring {@link GameServer} instances.
 * <p>
 * Supports creating fresh games as well as recovering crashed games,
 * with options for remote persistence or local persistence.
 */
public class GameServerFactory {
    private static final String MAP_PATH_FORMAT = "maps/%s.json";

    /**
     * Creates a standard {@link GameServer} configured with HTTP persistence and external orchestration.
     *
     * @param matchId         the unique identifier for the match
     * @param mapName         the name of the map to load
     * @param tcpPort         the port bound for TCP communication
     * @param udpPort         the port bound for UDP communication
     * @param backupEndpoint  the URI endpoint where periodic game state snapshots will be sent
     * @param resultsEndpoint the URI endpoint where final match results will be posted on game end
     * @param orchestrator    the orchestrator responsible for managing server heartbeats with remote services
     * @return a fully assembled and wired {@link GameServer} instance
     */
    public static GameServer createWithHttpPersistenceAndHeartbeat(
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
        return assemble(matchId, mapName, false, null, tcpPort, udpPort, persistence, orchestrator);
    }

    /**
     * Creates a lightweight {@link GameServer} using local file-system persistence and dummy orchestration.
     *
     * @param matchId the unique identifier for the match
     * @param mapName the name of the map to load
     * @param tcpPort the port bound for TCP communication
     * @param udpPort the port bound for UDP communication
     * @return a fully assembled {@link GameServer} writing data locally to {@code .temp/matches/}
     */
    public static GameServer createWithLocalPersistence(
            String matchId,
            String mapName,
            int tcpPort,
            int udpPort
    ) {
        GamePersistenceManager persistence = new GamePersistenceManager(
                new LocalGameBackupService(),
                new LocalGameResultsService()
        );
        return assemble(matchId, mapName, false, null, tcpPort, udpPort, persistence, new DummyGameServerOrchestrator());
    }

    /**
     * Recovers an interrupted {@link GameServer} match from a stored snapshot, using HTTP persistence.
     *
     * @param matchId         the unique identifier of the match to restore
     * @param tcpPort         the port bound for TCP communication
     * @param udpPort         the port bound for UDP communication
     * @param backupEndpoint  the URI endpoint where subsequent periodic game state snapshots will be sent
     * @param resultsEndpoint the URI endpoint where final match results will be posted on game end
     * @param repository      the repository implementation used to fetch the latest {@link MatchSnapshot}
     * @param orchestrator    the orchestrator responsible for managing server lifecycle with remote services
     * @return a recovered {@link GameServer} with restored state
     * @throws IllegalStateException if no snapshot can be found for the given {@code matchId}
     */
    public static GameServer createRecoveryServerWithHttpPersistenceAndHeartbeat(
            String matchId,
            int tcpPort,
            int udpPort,
            URI backupEndpoint,
            URI resultsEndpoint,
            GameSnapshotRepository repository,
            GameServerOrchestrator orchestrator
    ) {
        GamePersistenceManager persistence = new GamePersistenceManager(
                new HttpGameBackupService(),
                new HttpGameResultsService()
        );
        return assemble(matchId, null, true, repository, tcpPort, udpPort, persistence, orchestrator);
    }

    /**
     * Recovers a crashed {@link GameServer} game from local file-system storage (.temp/matches/).
     *
     * @param matchId the unique identifier of the match to restore
     * @param tcpPort the port bound for TCP communication
     * @param udpPort the port bound for UDP communication
     * @return a recovered {@link GameServer} pre-populated from the latest local snapshot
     * @throws IllegalStateException if no snapshot can be found for the given {@code matchId}
     */
    public static GameServer createRecoveryServerWithLocalPersistence(
            String matchId,
            int tcpPort,
            int udpPort
    ) {
        GamePersistenceManager persistence = new GamePersistenceManager(
                new LocalGameBackupService(),
                new LocalGameResultsService()
        );
        return assemble(matchId, null, true, new LocalGameSnapshotRepository(), tcpPort, udpPort, persistence, new DummyGameServerOrchestrator());
    }


    /**
     * Centralized assembly pipeline for all game server creation routes.
     *
     * @param matchId      the match identifier
     * @param mapName      the map file name (required if {@code isRecovery} is false)
     * @param isRecovery   {@code true} if restoring state from snapshot, {@code false} for new matches
     * @param repository   the snapshot repository (required if {@code isRecovery} is true)
     * @param tcpPort      TCP port for gateway binding
     * @param udpPort      UDP port for gateway binding
     * @param persistence  the configured persistence manager instance
     * @param orchestrator the server lifecycle/heartbeat orchestrator instance
     * @return fully constructed and wired {@link GameServer}
     */
    private static GameServer assemble(
            String matchId,
            String mapName,
            boolean isRecovery,
            GameSnapshotRepository repository,
            int tcpPort,
            int udpPort,
            GamePersistenceManager persistence,
            GameServerOrchestrator orchestrator
    ) {
        MatchSnapshot snapshot = null;
        GameContext gameContext;
        if (isRecovery) {
            if (repository == null) {
                throw new IllegalArgumentException("Snapshot repository cannot be null for recovery mode");
            }
            snapshot = repository.findLatestSnapshot(matchId)
                    .orElseThrow(() -> new IllegalStateException("Cannot recover match " + matchId + ": No snapshot found"));
            gameContext = GameContextFactory.createFromDTO(snapshot.context(), new GameEntityFactoryImpl());
        } else {
            gameContext = GameContextFactory.createFromMap(MAP_PATH_FORMAT.formatted(mapName), new GameEntityFactoryImpl());
        }

        Game game = new GameImpl(gameContext);
        GameSessionController sessionController = new GameSessionController();
        NettyGameServerGateway gateway = new NettyGameServerGateway(tcpPort, udpPort, sessionController);
        ServerGameEngine engine = new ServerGameEngine(game);

        MatchLifecycleManager lifecycleManager = isRecovery
                ? new RecoveryMatchLifecycleManager(snapshot.activePlayers(), engine, gateway)
                : new StandardMatchLifecycleManager(4, engine, gateway);

        GameServer server = new GameServerImpl(
                matchId,
                engine,
                gateway,
                persistence,
                orchestrator,
                lifecycleManager
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