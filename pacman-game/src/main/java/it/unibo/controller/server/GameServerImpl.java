package it.unibo.controller.server;

import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.server.orchestration.GameServerOrchestrator;
import it.unibo.controller.server.persistence.GamePersistenceManager;
import it.unibo.controller.shared.engine.GameEndedEvent;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.engine.GameLifecycleEvent;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.sockets.packets.GameContextPacket;
import it.unibo.controller.shared.network.sockets.packets.GameEndPacket;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import it.unibo.controller.shared.network.translation.GameContextEncoder;
import it.unibo.controller.shared.network.translation.GameContextEncoderImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class GameServerImpl implements GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServerImpl.class);

    private final GameServerGateway gateway;
    private final GameEngine engine;
    private final GamePersistenceManager persistenceManager;
    private final GameContextEncoder encoder = new GameContextEncoderImpl();

    private final GameServerOrchestrator orchestrator;

    private final FourManLobby lobby = new FourManLobby();

    public GameServerImpl(GameEngine engine,
                          GameServerGateway gateway,
                          GamePersistenceManager persistenceManager,
                          GameServerOrchestrator orchestrator
    ) {
        this.gateway = gateway;
        this.engine = engine;
        this.persistenceManager = persistenceManager;
        this.orchestrator = orchestrator;
    }

    @Override
    public void start() throws Exception {
        try {
            gateway.start();
            orchestrator.ready();
            orchestrator.startHeartbeat();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        try {
            orchestrator.stopHeartbeat();
            engine.stop();
            gateway.stop();
            persistenceManager.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* ********************************** *
     * Session lifecycle listener methods *
     * ********************************** */

    @Override
    public void onPlayerConnected(GameSession session) {
        String username = session.getUsername();
        if (lobby.isPlaying()) {
            logger.info("Player {} tried to join after game already started", username);
            return;
        }
        lobby.addPlayer(username);
        logger.info("Player {} joined ({}/{})", username, lobby.getCurrentPlayerCount(), lobby.getRequiredPlayerCount());
        if (lobby.isFull()) {
            lobby.setPlaying(true);
            startGame();
        }
    }

    @Override
    public void onPlayerReconnected(GameSession session) {
        String username = session.getUsername();
        if (lobby.isPlaying()) {
            logger.info("Player {} has reconnected mid-game, restoring human control", username);
            Game game = engine.getGame();
            game.changePacmanBehaviour(session.getUsername(), true);
        } else {
            lobby.addPlayer(username);
            logger.info("Player {} has reconnected to the lobby before the game started {}/{}",
                    username, lobby.getCurrentPlayerCount(), lobby.getRequiredPlayerCount());
            if (lobby.isFull()) {
                lobby.setPlaying(true);
                startGame();
            }
        }
    }

    @Override
    public void onPlayerDisconnected(GameSession session) {
        String username = session.getUsername();
        if (lobby.isPlaying()) {
            logger.info("Player {} has disconnected mid-game, substituting with a bot", username);
            Game game = engine.getGame();
            game.changePacmanBehaviour(username, false);
        } else {
            lobby.removePlayer(username);
            logger.info("Player {} has left the lobby before game started {}/{}",
                    username, lobby.getCurrentPlayerCount(), lobby.getRequiredPlayerCount());
        }
    }

    private void startGame() {
        List<String> players = lobby.getPlayers();
        logger.info("Required player count reached. Starting game with players: {}", players);
        engine.getGame().setPacmanNames(players);
        GameContextDTO dto = encoder.encode(engine.getGame().getContext());
        gateway.broadcastTcp(new GameContextPacket(dto));
        gateway.broadcastTcp(new GameStartPacket());
        engine.start();
        persistenceManager.start();
    }

    /* ******************************** *
     * Network command listener methods *
     * ******************************** */

    @Override
    public void onCommandReceived(PacmanMoveCommand command) {
        logger.debug("Received move command: {}", command);
        engine.enqueueCommand(command);
    }

    /* **************************** *
     * Game engine listener methods *
     * **************************** */

    /*
     * When a new game snapshot is available it has to be broadcast to all clients via UDP.
     * Such snapshot also needs to be provided to the persistence manager
     */
    @Override
    public void onGameContextUpdate(GameContext context) {
        logger.trace("Broadcasting game context to all clients");
        GameContextDTO dto = encoder.encode(context);
        persistenceManager.updateContext(dto);
        gateway.broadcastUdp(new GameContextPacket(dto));
    }

    /*
     * Once a game has ended, the persistence manager needs to save the results.
     * The clients need to receive the last authoritative game state and be notified that the game has ended via TCP.
     */
    @Override
    public void onGameEvent(GameLifecycleEvent event) {
        if (event instanceof GameEndedEvent(GameContext context)) {
            logger.info("Game has ended");
            GameContextDTO dto = encoder.encode(context);
            persistenceManager.saveFinalSnapshot(dto);
            gateway.broadcastTcp(new GameContextPacket(dto));
            gateway.broadcastTcp(new GameEndPacket());
            lobby.setPlaying(false);
            orchestrator.shutdown();
            scheduleServerShutdown(Duration.ofSeconds(10));
        }
    }

    private void scheduleServerShutdown(Duration delay) {
        logger.info("Server process scheduled to terminate in {} seconds", delay.getSeconds());
        try {
            Thread.sleep(delay.toMillis());
            stop();
            logger.info("Exiting JVM process cleanly");
            System.exit(0);
        } catch (Exception e) {
            logger.error("Error occurred while shutting down the server components", e);
            System.exit(1);
        }
    }
}