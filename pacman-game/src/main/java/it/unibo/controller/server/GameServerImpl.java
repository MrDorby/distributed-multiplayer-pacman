package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.lobby.LobbyState;
import it.unibo.controller.server.lobby.LobbyManager;
import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.server.orchestration.GameServerOrchestrator;
import it.unibo.controller.server.persistence.GamePersistenceManager;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.shared.engine.event.GameEndedEvent;
import it.unibo.controller.shared.engine.event.GameEvent;
import it.unibo.controller.shared.engine.command.PacmanMoveCommand;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.sockets.packets.GameContextPacket;
import it.unibo.controller.shared.network.sockets.packets.GameEndPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServerImpl implements GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServerImpl.class);
    public static final int SERVER_SHUTDOWN_DELAY_SECONDS = 10;

    private final String matchId;
    private final GameServerGateway gateway;
    private final ServerGameEngine engine;
    private final GamePersistenceManager persistenceManager;
    private final GameServerOrchestrator orchestrator;

    private final LobbyManager lobbyManager;

    private final ScheduledExecutorService shutdownScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService networkWorker = Executors.newSingleThreadExecutor();

    public GameServerImpl(
            String matchId,
            ServerGameEngine engine,
            GameServerGateway gateway,
            GamePersistenceManager persistenceManager,
            GameServerOrchestrator orchestrator,
            LobbyManager lobbyManager
    ) {
        this.matchId = matchId;
        this.engine = engine;
        this.gateway = gateway;
        this.persistenceManager = persistenceManager;
        this.orchestrator = orchestrator;
        this.lobbyManager = lobbyManager;
    }

    @Override
    public void start() throws Exception {
        try {
            gateway.start();
            persistenceManager.start();
            orchestrator.ready();
            orchestrator.startHeartbeat();
            lobbyManager.onServerStart();
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
            persistenceManager.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            networkWorker.shutdown();
            shutdownScheduler.shutdown();
        }
    }

    /* ********************************** *
     * Session lifecycle listener methods *
     * ********************************** */

    @Override
    public void onPlayerConnected(GameSession session) {
        lobbyManager.onPlayerConnected(session);
    }

    @Override
    public void onPlayerReconnected(GameSession session) {
        lobbyManager.onPlayerReconnected(session);
    }

    @Override
    public void onPlayerDisconnected(GameSession session) {
        lobbyManager.onPlayerDisconnected(session);
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
        networkWorker.submit(() -> {
            logger.trace("Broadcasting game context to all clients");
            MatchSnapshot snapshot = new MatchSnapshot(this.matchId, System.currentTimeMillis(), context);
            persistenceManager.updateSnapshot(snapshot);
            gateway.broadcastUdp(new GameContextPacket(context));
        });
    }

    /*
     * Once a game has ended, the persistence manager needs to save the results.
     * The clients need to receive the last authoritative game state and be notified that the game has ended via TCP.
     */
    @Override
    public void onGameEvent(GameEvent event) {
        lobbyManager.setState(LobbyState.FINISHED);
        if (event instanceof GameEndedEvent(GameContextDTO context)) {
            networkWorker.submit(() -> {
                logger.info("Game has ended");
                MatchSnapshot snapshot = new MatchSnapshot(this.matchId, System.currentTimeMillis(), context);
                persistenceManager.saveFinalSnapshot(snapshot);
                gateway.broadcastTcp(new GameContextPacket(context));
                gateway.broadcastTcp(new GameEndPacket(context));
                orchestrator.shutdown();
                scheduleServerShutdown(Duration.ofSeconds(SERVER_SHUTDOWN_DELAY_SECONDS));
            });
        }
    }

    private void scheduleServerShutdown(Duration delay) {
        logger.info("Server process scheduled to terminate in {} seconds", delay.getSeconds());
        shutdownScheduler.schedule(() -> {
            try {
                stop();
                logger.info("Exiting JVM process cleanly");
                System.exit(0);
            } catch (Exception e) {
                logger.error("Error occurred while shutting down the server components", e);
                System.exit(1);
            }
        }, delay.toMillis(), TimeUnit.MILLISECONDS);
    }
}