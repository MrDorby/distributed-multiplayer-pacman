package it.unibo.controller.server;

import it.unibo.controller.server.health.GameServerHealthCheckHandler;
import it.unibo.controller.server.health.GameServerStatus;
import it.unibo.controller.server.network.http.GameHttpServer;
import it.unibo.controller.server.network.transport.NettyGameNetworkServer;
import it.unibo.controller.server.network.transport.NettyGameNetworkServerFactory;
import it.unibo.controller.server.persistence.GamePersistenceCoordinator;
import it.unibo.controller.server.persistence.backup.GameBackupService;
import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.engine.GameLifecycleListener;
import it.unibo.controller.server.persistence.results.GameResultsService;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.packets.GameStartPacket;
import it.unibo.controller.shared.network.packets.PacketType;
import it.unibo.controller.shared.network.translation.GameContextEncoder;
import it.unibo.controller.shared.network.translation.GameContextEncoderImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Top-level coordinator for a single authoritative game server session.
 */
public class GameServerController implements GameServerNetworkListener, GameContextBroadcaster, GameLifecycleListener {
    private static final Logger logger = LoggerFactory.getLogger(GameServerController.class);
    private static final int REQUIRED_PLAYERS = 4;

    private final NettyGameNetworkServer tcpUdpServer;
    private final GameEngine engine;
    private final GamePersistenceCoordinator persistenceCoordinator;
    private final GameHttpServer httpServer;
    private final GameContextEncoder encoder = new GameContextEncoderImpl();
    private final PlayerLobby lobby = new PlayerLobby(REQUIRED_PLAYERS);

    private final int tcpPort;
    private final int udpPort;
    private final int httpPort;

    public GameServerController(Game game, int tcpPort, int udpPort, int httpPort,
                                GameBackupService backupService, GameResultsService resultsService) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.httpPort = httpPort;
        this.tcpUdpServer = NettyGameNetworkServerFactory.create(tcpPort, udpPort, this);
        this.engine = new ServerGameEngine(game, this);
        this.persistenceCoordinator = new GamePersistenceCoordinator(backupService, resultsService);
        this.httpServer = new GameHttpServer(httpPort);
        this.httpServer.addGetEndpoint("/health", new GameServerHealthCheckHandler(this::getCurrentHealthStatus));
    }

    public void start() throws Exception {
        try {
            httpServer.start();
            tcpUdpServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Server started. TCP port: {}, UDP port: {}, HTTP port: {}", tcpPort, udpPort, httpPort);
    }

    // TODO add shutdown

    @Override
    public void onPlayerJoined(String username) {
        if (lobby.lobbyIsPlaying()) {
            logger.warn("Player {} tried to join after game already started.", username);
            return;
        }
        boolean lobbyFull = lobby.tryJoin(username);
        logger.info("Player {} joined ({}/{}).", username, lobby.joinedCount(), lobby.requiredPlayers());
        if (lobbyFull) {
            startGame();
        }
    }

    private void startGame() {
        List<String> players = lobby.joinedUsernames();
        logger.info("Required player count reached. Starting game with players: {}", players);
        engine.getGame().setPacmanNames(players);
        GameContextDTO initialDto = encoder.encode(engine.getGame().getContext());
        tcpUdpServer.broadcastTcp(PacketType.GAME_CONTEXT.getId(), initialDto);
        tcpUdpServer.broadcastTcp(PacketType.GAME_START.getId(), new GameStartPacket());
        engine.start();
        persistenceCoordinator.start();
    }

    @Override
    public void onCommandReceived(String username, PacmanMoveCommand command) {
        logger.debug("Received command from {}: {}", username, command);
        engine.enqueueCommand(command);
    }

    @Override
    public void onGameEnded(GameContext finalContext) {
        GameContextDTO dto = encoder.encode(finalContext);
        persistenceCoordinator.onGameEnded(dto);
    }

    @Override
    public void broadcast(GameContext context) {
        if (lobby.lobbyIsPlaying()) {
            GameContextDTO dto = encoder.encode(context);
            persistenceCoordinator.updateContext(dto);
            tcpUdpServer.broadcastUdp(PacketType.GAME_CONTEXT.getId(), dto);
            logger.debug("Broadcasted game context to all sessions.");
        }
    }

    private GameServerStatus getCurrentHealthStatus() {
        return new GameServerStatus(lobby.lobbyIsPlaying(), lobby.joinedCount(), lobby.requiredPlayers(), engine.isRunning());
    }
}