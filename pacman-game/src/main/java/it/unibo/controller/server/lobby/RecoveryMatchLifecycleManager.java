package it.unibo.controller.server.lobby;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.shared.engine.command.ChangePacmanBehaviourCommand;
import it.unibo.controller.shared.network.sockets.packets.GameContextPacket;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages the lifecycle of a recovered match.
 * <p>
 * Grants a 10-second grace window for previous active members to reconnect before automatically starting
 * with bot substitutes for missing players.
 */
public class RecoveryMatchLifecycleManager implements MatchLifecycleManager {
    private static final Logger logger = LoggerFactory.getLogger(RecoveryMatchLifecycleManager.class);
    private static final int RECOVERY_WINDOW_SECONDS = 10;

    private final ServerGameEngine engine;
    private final GameServerGateway gateway;

    private final Set<String> previousActivePlayers;
    private final Set<String> connectedPlayers = new HashSet<>();
    private LobbyState state = LobbyState.WAITING;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> matchStartTask;

    public RecoveryMatchLifecycleManager(
            Collection<String> previousActivePlayers,
            ServerGameEngine engine,
            GameServerGateway gateway
    ) {
        this.previousActivePlayers = Set.copyOf(previousActivePlayers);
        this.engine = engine;
        this.gateway = gateway;
    }

    @Override
    public synchronized LobbyState getState() {
        return state;
    }

    @Override
    public synchronized void setState(LobbyState state) {
        this.state = state;
    }

    @Override
    public Collection<String> getActivePlayers() {
        return List.copyOf(previousActivePlayers);
    }

    /**
     * Arms the grace window timer. Starts the game when the window expires or all players join.
     */
    @Override
    public synchronized void onServerStart() {
        logger.info("Recovery server ready. Starting {}-second reconnection grace window...", RECOVERY_WINDOW_SECONDS);
        this.matchStartTask = scheduler.schedule(
                this::startGame,
                RECOVERY_WINDOW_SECONDS,
                TimeUnit.SECONDS
        );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Re-registers previous active players during the grace window, or restores human control if the player connects
     * after the game has already started.
     */
    @Override
    public synchronized void onPlayerConnected(GameSession session) {
        String username = session.getUsername();
        if (!previousActivePlayers.contains(username)) {
            logger.warn("Unexpected player {} tried to connect", username);
            return;
        }
        switch (state) {
            case WAITING -> {
                connectedPlayers.add(username);
                logger.info("Player {} joined pre-game lobby ({}/{})",
                        username, connectedPlayers.size(), previousActivePlayers.size());
                if (connectedPlayers.size() == previousActivePlayers.size()) {
                    startGame();
                }
            }
            case PLAYING -> {
                logger.info("Player {} connected mid-game. Restoring human control", username);
                connectedPlayers.add(username);
                engine.enqueueCommand(new ChangePacmanBehaviourCommand(username, true));
                gateway.sendTcp(username, new GameContextPacket(engine.getLatestContext()));
                gateway.sendTcp(username, new GameStartPacket());
            }
            case FINISHED -> logger.info("Player {} tried to join but match is finished", username);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Restores session state for roster players reconnecting during recovery.
     */
    @Override
    public synchronized void onPlayerReconnected(GameSession session) {
        String username = session.getUsername();
        if (!previousActivePlayers.contains(username)) {
            logger.warn("Unexpected player {} attempted to reconnect", username);
            return;
        }
        switch (state) {
            case WAITING -> {
                connectedPlayers.add(username);
                logger.info("Player {} reconnected to pre-game lobby ({}/{})",
                        username, connectedPlayers.size(), previousActivePlayers.size());
                if (connectedPlayers.size() == previousActivePlayers.size()) {
                    startGame();
                }
            }
            case PLAYING -> {
                logger.info("Player {} reconnected mid-game, restoring human control", username);
                connectedPlayers.add(username);
                engine.enqueueCommand(new ChangePacmanBehaviourCommand(username, true));
                gateway.sendTcp(username, new GameContextPacket(engine.getLatestContext()));
                gateway.sendTcp(username, new GameStartPacket());
            }
            case FINISHED -> logger.info("Player {} tried to reconnect but the game has already finished", username);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Clears player connection status, assigning an AI bot if the match is actively running.
     */
    @Override
    public synchronized void onPlayerDisconnected(GameSession session) {
        String username = session.getUsername();
        if (!previousActivePlayers.contains(username)) {
            return;
        }
        switch (state) {
            case WAITING -> {
                connectedPlayers.remove(username);
                logger.info("Player {} left pre-game lobby ({}/{})", username, connectedPlayers.size(), previousActivePlayers.size());
            }
            case PLAYING -> {
                connectedPlayers.remove(username);
                logger.info("Player {} disconnected mid-game, substituting with bot", username);
                engine.enqueueCommand(new ChangePacmanBehaviourCommand(username, false));
            }
            case FINISHED -> logger.info("Player {} disconnected after the game has ended", username);
        }
    }

    private void startGame() {
        setState(LobbyState.PLAYING);
        if (matchStartTask != null && !matchStartTask.isDone()) {
            matchStartTask.cancel(false);
        }
        logger.info("Starting recovered game for {}", previousActivePlayers);
        for (String player : previousActivePlayers) {
            boolean isConnected = connectedPlayers.contains(player);
            logger.info("Player {} starting as {}", player, isConnected ? "Human" : "Bot");
            engine.enqueueCommand(new ChangePacmanBehaviourCommand(player, isConnected));
        }
        engine.start();
        gateway.broadcastTcp(new GameContextPacket(engine.getLatestContext()));
        gateway.broadcastTcp(new GameStartPacket());
        scheduler.shutdown();
    }
}
