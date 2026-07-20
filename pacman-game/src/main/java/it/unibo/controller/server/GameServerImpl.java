package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.server.orchestration.GameServerOrchestrator;
import it.unibo.controller.server.persistence.GamePersistenceManager;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.shared.engine.event.GameEndedEvent;
import it.unibo.controller.shared.engine.event.GameEvent;
import it.unibo.controller.shared.engine.command.ChangePacmanBehaviourCommand;
import it.unibo.controller.shared.engine.command.PacmanMoveCommand;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.sockets.packets.GameContextPacket;
import it.unibo.controller.shared.network.sockets.packets.GameEndPacket;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class GameServerImpl implements GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServerImpl.class);

    private final String matchId;

    private final GameServerGateway gateway;
    private final ServerGameEngine engine;
    private final GamePersistenceManager persistenceManager;
    private final GameServerOrchestrator orchestrator;

    private final FourManLobby lobby = new FourManLobby();

    public GameServerImpl(
            String matchId,
            ServerGameEngine engine,
            GameServerGateway gateway,
            GamePersistenceManager persistenceManager,
            GameServerOrchestrator orchestrator
    ) {
        this.matchId = matchId;
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
    public synchronized void onPlayerConnected(GameSession session) {
        String username = session.getUsername();
        switch (lobby.getState()) {
            case WAITING -> {
                lobby.addPlayer(username);
                logger.info("Player {} joined ({}/{})", username, lobby.getCurrentPlayerCount(), lobby.getRequiredPlayerCount());
                if (lobby.isFull()) {
                    lobby.setState(LobbyState.PLAYING);
                    startGame();
                }
            }
            case PLAYING -> logger.info("Player {} tried to join after game already started", username);
            case FINISHED -> logger.info("Player {} tried to join, but the game has already finished", username);
        }
    }

    @Override
    public synchronized void onPlayerReconnected(GameSession session) {
        String username = session.getUsername();
        switch (lobby.getState()) {
            case WAITING -> {
                lobby.addPlayer(username);
                logger.info("Player {} has reconnected to the lobby before the game started ({}/{})",
                        username, lobby.getCurrentPlayerCount(), lobby.getRequiredPlayerCount());
                if (lobby.isFull()) {
                    lobby.setState(LobbyState.PLAYING);
                    startGame();
                }
            }
            case PLAYING -> {
                logger.info("Player {} has reconnected mid-game, restoring human control", username);
                engine.enqueueCommand(new ChangePacmanBehaviourCommand(username, true));
                GameContextDTO context = engine.getLatestContext();
                gateway.sendTcp(username, new GameContextPacket(context));
                gateway.sendTcp(username, new GameStartPacket());
            }
            case FINISHED -> logger.info("Player {} tried to reconnect, but the game has already finished", username);
        }
    }

    @Override
    public synchronized void onPlayerDisconnected(GameSession session) {
        String username = session.getUsername();
        switch (lobby.getState()) {
            case WAITING -> {
                lobby.removePlayer(username);
                logger.info("Player {} has left the lobby before game started ({}/{})",
                        username, lobby.getCurrentPlayerCount(), lobby.getRequiredPlayerCount());
            }
            case PLAYING -> {
                logger.info("Player {} has disconnected mid-game, substituting with a bot", username);
                engine.enqueueCommand(new ChangePacmanBehaviourCommand(username, false));
            }
            case FINISHED -> logger.info("Player {} disconnected after the game was already finished", username);
        }
    }

    private void startGame() {
        List<String> players = lobby.getPlayers();
        logger.info("Required player count reached. Starting game with players: {}", players);
        engine.initialize(players);
        GameContextDTO context = engine.getLatestContext();
        gateway.broadcastTcp(new GameContextPacket(context));
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
    public void onGameContextUpdate(GameContextDTO context) {
        logger.trace("Broadcasting game context to all clients");
        MatchSnapshot snapshot = new MatchSnapshot(this.matchId, System.currentTimeMillis(), context);
        persistenceManager.updateContext(snapshot);
        gateway.broadcastUdp(new GameContextPacket(context));
    }

    /*
     * Once a game has ended, the persistence manager needs to save the results.
     * The clients need to receive the last authoritative game state and be notified that the game has ended via TCP.
     */
    @Override
    public void onGameEvent(GameEvent event) {
        if (event instanceof GameEndedEvent(GameContextDTO context)) {
            logger.info("Game has ended");
            MatchSnapshot snapshot = new MatchSnapshot(this.matchId, System.currentTimeMillis(), context);
            persistenceManager.saveFinalSnapshot(snapshot);
            gateway.broadcastTcp(new GameContextPacket(context));
            gateway.broadcastTcp(new GameEndPacket(context));
            lobby.setState(LobbyState.FINISHED);
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