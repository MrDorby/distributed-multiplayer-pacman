package it.unibo.controller.server;

import it.unibo.controller.server.persistence.GamePersistenceCoordinator;
import it.unibo.controller.server.persistence.backup.GameBackupService;
import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.engine.GameLifecycleListener;
import it.unibo.controller.server.persistence.results.GameResultsService;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.server.network.GameContextBroadcaster;
import it.unibo.controller.server.network.GameServerNetworkListener;
import it.unibo.controller.server.network.NettyGameNetworkServer;
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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Top-level coordinator for a single authoritative game server session.
 */
public class GameContextServerController implements GameServerNetworkListener, GameContextBroadcaster, GameLifecycleListener {
    private static final Logger logger = LoggerFactory.getLogger(GameContextServerController.class);
    private static final int REQUIRED_PLAYERS = 4;
    private final NettyGameNetworkServer server;
    private final GameEngine engine;
    private final GamePersistenceCoordinator persistenceCoordinator;
    private final GameContextEncoder encoder = new GameContextEncoderImpl();
    private final List<String> joinedUsernames = new CopyOnWriteArrayList<>();
    private volatile boolean gameStarted = false;

    public GameContextServerController(Game game, int tcpPort, int udpPort,
                                       GameBackupService backupService, GameResultsService resultsService) throws Exception {
        this.server = new NettyGameNetworkServer(tcpPort, udpPort, this);
        this.engine = new ServerGameEngine(game, this);
        this.persistenceCoordinator = new GamePersistenceCoordinator(backupService, resultsService);
        server.start();
        logger.info("Server controller initialized. TCP port: {}, UDP port: {}", tcpPort, udpPort);
    }

    @Override
    public void onPlayerJoined(String username) {
        if (gameStarted) {
            logger.warn("Player {} tried to join after game already started; ignoring.", username);
            return;
        }
        joinedUsernames.add(username);
        logger.info("Player {} joined ({}/{}).", username, joinedUsernames.size(), REQUIRED_PLAYERS);
        if (joinedUsernames.size() == REQUIRED_PLAYERS) {
            startGame();
        }
    }

    private void startGame() {
        gameStarted = true;
        logger.info("Required player count reached. Starting game with players: {}", joinedUsernames);
        engine.getGame().setPacmanNames(List.copyOf(joinedUsernames));
        GameContextDTO initialDto = encoder.encode(engine.getGame().getContext());
        server.broadcastTcp(PacketType.GAME_CONTEXT.getId(), initialDto);
        server.broadcastTcp(PacketType.GAME_START.getId(), new GameStartPacket());
        engine.start();
        persistenceCoordinator.start();
        logger.info("Game engine started.");
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
        if (gameStarted) {
            GameContextDTO dto = encoder.encode(context);
            persistenceCoordinator.updateContext(dto);
            server.broadcastUdp(PacketType.GAME_CONTEXT.getId(), dto);
            logger.debug("Broadcasted game context to all sessions.");
        }
    }
}