package it.unibo.controller.server;

import it.unibo.controller.server.network.sockets.GameNetworkServer;
import it.unibo.controller.server.network.sockets.GameNetworkServerFactory;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.server.persistence.GamePersistenceCoordinator;
import it.unibo.controller.server.persistence.backup.GameBackupService;
import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.persistence.results.GameResultsService;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.sockets.packets.GameContextPacket;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
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
public class GameServerControllerImpl implements GameServerController {
    private static final Logger logger = LoggerFactory.getLogger(GameServerControllerImpl.class);
    private static final int REQUIRED_PLAYERS = 4;

    private final GameNetworkServer tcpUdpServer;
    private final GameEngine engine;
    private final GamePersistenceCoordinator persistenceCoordinator;
    private final GameContextEncoder encoder = new GameContextEncoderImpl();
    private final PlayerLobby lobby = new PlayerLobby(REQUIRED_PLAYERS);

    private final int tcpPort;
    private final int udpPort;

    public GameServerControllerImpl(Game game, int tcpPort, int udpPort,
                                    GameBackupService backupService, GameResultsService resultsService
    ) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.tcpUdpServer = GameNetworkServerFactory.create(tcpPort, udpPort, this);
        this.engine = new ServerGameEngine(game, this);
        this.persistenceCoordinator = new GamePersistenceCoordinator(backupService, resultsService);
    }

    @Override
    public void start() throws Exception {
        try {
            tcpUdpServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Server started. TCP port: {}, UDP port: {}", tcpPort, udpPort);
    }

    @Override
    public void stop() throws Exception {
        try {
            tcpUdpServer.stop();
            engine.stop();
            persistenceCoordinator.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Session handling

    @Override
    public void onPlayerConnected(GameSession session) {
        String username = session.getUsername();
        if (lobby.lobbyIsPlaying()) {
            logger.info("Player {} tried to join after game already started", username);
            return;
        }
        boolean lobbyFull = lobby.join(username);
        logger.info("Player {} joined ({}/{})", username, lobby.joinedCount(), lobby.requiredPlayers());
        if (lobbyFull) {
            startGame();
        }
    }

    private void startGame() {
        List<String> players = lobby.joinedPlayers();
        logger.info("Required player count reached. Starting game with players: {}", players);
        engine.getGame().setPacmanNames(players);
        GameContextDTO initialDto = encoder.encode(engine.getGame().getContext());
        tcpUdpServer.broadcastTcp(new GameContextPacket(initialDto));
        tcpUdpServer.broadcastTcp(new GameStartPacket());
        engine.start();
        persistenceCoordinator.start();
    }

    @Override
    public void onPlayerReconnected(GameSession session) {
        // the game needs to know that it has to override the pacman status to human, not bot
    }

    @Override
    public void onPlayerDisconnect(GameSession session) {
        // the game needs to set the pacman originally controlled by the player as bot.
    }

    // Command handling

    @Override
    public void onCommandReceived(PacmanMoveCommand command) {
        logger.debug("Received move command: {}", command);
        engine.enqueueCommand(command);
    }

    // Game logic handling

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
            tcpUdpServer.broadcastUdp(new GameContextPacket(dto));
            logger.debug("Broadcasted game context to all sessions.");
        }
    }
}