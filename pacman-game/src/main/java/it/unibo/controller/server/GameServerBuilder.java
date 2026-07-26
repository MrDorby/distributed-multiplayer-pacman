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
import it.unibo.controller.server.persistence.results.GameResultsRepository;
import it.unibo.controller.server.persistence.results.LocalGameResultsRepository;
import it.unibo.controller.server.persistence.results.MongoGameResultsRepository;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;

/**
 * Factory class responsible for assembling and configuring {@link GameServer} instances.
 * <p>
 * Supports creating fresh games as well as recovering crashed games,
 * with options for remote persistence or local persistence.
 */
public class GameServerBuilder {
    private static final String MAP_PATH_FORMAT = "maps/%s.json";

    private String matchId;
    private String mapName;
    private int tcpPort;
    private int udpPort;
    private boolean isRecovery = false;
    private GameSnapshotRepository snapshotRepository;
    private GameResultsRepository resultsService;
    private GameServerOrchestrator orchestrator;

    public GameServerBuilder forMatch(String matchId) {
        this.matchId = matchId;
        return this;
    }

    public GameServerBuilder withMap(String mapName) {
        this.mapName = mapName;
        return this;
    }

    public GameServerBuilder withPorts(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        return this;
    }

    public GameServerBuilder asRecovery() {
        this.isRecovery = true;
        return this;
    }

    public GameServerBuilder withLocalPersistence() {
        this.snapshotRepository = new LocalGameSnapshotRepository();
        this.resultsService = new LocalGameResultsRepository();
        return this;
    }

    public GameServerBuilder withRemotePersistence() {
        GameServicesConfig config = GameServicesConfig.fromEnv();
        this.snapshotRepository = new MongoGameSnapshotRepository(config.backup().endpoint());
        this.resultsService = new MongoGameResultsRepository(config.results().endpoint());
        return this;
    }

    public GameServerBuilder withOrchestrator(GameServerOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        return this;
    }

    public GameServer build() {
        if (orchestrator == null) {
            orchestrator = new DummyGameServerOrchestrator();
        }
        GamePersistenceManager persistence = new GamePersistenceManager(snapshotRepository, resultsService);
        MatchSnapshot snapshot = null;
        GameContext gameContext;
        if (isRecovery) {
            snapshot = snapshotRepository.findLatestSnapshot(matchId)
                    .join()
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