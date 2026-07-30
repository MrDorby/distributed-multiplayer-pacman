package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.lobby.LobbyManager;
import it.unibo.controller.server.lobby.FixedMatchLobbyManager;
import it.unibo.controller.server.network.sockets.NettyGameServerGateway;
import it.unibo.controller.server.network.sockets.handlers.*;
import it.unibo.controller.server.network.sockets.session.GameSessionController;
import it.unibo.controller.server.orchestration.DummyGameServerOrchestrator;
import it.unibo.controller.server.orchestration.GameServerOrchestrator;
import it.unibo.controller.server.persistence.GamePersistenceManager;
import it.unibo.controller.server.persistence.snapshot.*;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.match.GameMatchRepository;
import it.unibo.controller.server.persistence.match.LocalGameMatchRepository;
import it.unibo.controller.server.persistence.match.MongoGameMatchRepository;
import it.unibo.controller.server.persistence.results.GameResultsRepository;
import it.unibo.controller.server.persistence.results.LocalGameResultsRepository;
import it.unibo.controller.server.persistence.results.MongoGameResultsRepository;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;

import java.util.List;

/**
 * Factory class responsible for assembling and configuring {@link GameServer} instances.
 * <p>
 * Supports creating fresh games as well as recovering crashed games,
 * with options for remote persistence or local persistence.
 */
public class GameServerBuilder {

    private static final String MAP_PATH_FORMAT = "maps/%s.json";
    private static final int DEFAULT_WHITELIST_LOBBY_TIMEOUT_SECONDS = 10;

    private String matchId;
    private String mapName;
    private int tcpPort;
    private int udpPort;

    private boolean isRecovery = false;
    private boolean isLocalPersistence = false;

    private GameSnapshotRepository snapshotRepository;
    private GameResultsRepository resultsRepository;
    private GameMatchRepository matchRepository;
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

    public GameServerBuilder asWhitelisted() {
        this.isRecovery = false;
        return this;
    }

    public GameServerBuilder asRecovery() {
        this.isRecovery = true;
        return this;
    }

    public GameServerBuilder withLocalPersistence() {
        this.isLocalPersistence = true;
        return this;
    }

    public GameServerBuilder withRemotePersistence() {
        this.isLocalPersistence = false;
        return this;
    }

    public GameServerBuilder withOrchestrator(GameServerOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        return this;
    }

    public GameServer build() {
        configureRepositories();
        if (orchestrator == null) {
            orchestrator = new DummyGameServerOrchestrator();
        }
        GamePersistenceManager persistence = new GamePersistenceManager(snapshotRepository, resultsRepository);
        GameContext gameContext;

        if (isRecovery) {
            MatchSnapshot snapshot = snapshotRepository.findLatestSnapshot(matchId)
                    .join()
                    .orElseThrow(() -> new IllegalStateException("Cannot recover match " + matchId));
            gameContext = GameContextFactory.createFromDTO(snapshot.context(), new GameEntityFactoryImpl());
        } else {
            gameContext = GameContextFactory.createFromMap(MAP_PATH_FORMAT.formatted(mapName), new GameEntityFactoryImpl());
        }

        Game game = new GameImpl(gameContext);

        List<String> expectedPlayers = fetchExpectedPlayers();

        GameSessionController sessionController = new GameSessionController(expectedPlayers);
        NettyGameServerGateway gateway = new NettyGameServerGateway(tcpPort, udpPort, sessionController);
        ServerGameEngine engine = new ServerGameEngine(game);

        if (!isRecovery) {
            engine.initialize(expectedPlayers);
        }

        LobbyManager lobbyManager = new FixedMatchLobbyManager(
                expectedPlayers,
                DEFAULT_WHITELIST_LOBBY_TIMEOUT_SECONDS,
                engine,
                gateway
        );

        GameServer server = new GameServerImpl(
                matchId,
                engine,
                gateway,
                persistence,
                orchestrator,
                lobbyManager
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

    private void configureRepositories() {
        if (isLocalPersistence) {
            this.snapshotRepository = new LocalGameSnapshotRepository();
            this.resultsRepository = new LocalGameResultsRepository();
            this.matchRepository = new LocalGameMatchRepository();
        } else {
            GameServicesConfig config = GameServicesConfig.fromEnv();
            this.snapshotRepository = new MongoGameSnapshotRepository(config.backup().endpoint());
            this.resultsRepository = new MongoGameResultsRepository(config.results().endpoint());
            this.matchRepository = new MongoGameMatchRepository(config.backup().endpoint());
        }
    }

    private List<String> fetchExpectedPlayers() {
        return matchRepository.findExpectedPlayers(matchId)
                .join()
                .orElseThrow(() -> new IllegalStateException("No player list found in repository for match " + matchId));
    }
}