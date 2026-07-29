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
 * Manages the lobby for a match bound to a fixed list of players (matchmaker match or recovered match).
 * <p>
 * Grants a configurable grace window for expected players to join or reconnect.
 * Automatically starts the game once all expected players connect or when the timer expires.
 */
public class WhitelistedLobbyManager implements LobbyManager {
    private static final Logger logger = LoggerFactory.getLogger(WhitelistedLobbyManager.class);

    private final int connectionTimeoutSeconds;
    private final ServerGameEngine engine;
    private final GameServerGateway gateway;

    private final Set<String> expectedPlayers;
    private final Set<String> connectedPlayers = new HashSet<>();
    private LobbyState state = LobbyState.WAITING;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> matchStartTask;

    public WhitelistedLobbyManager(
            Collection<String> expectedPlayers,
            int connectionTimeoutSeconds,
            ServerGameEngine engine,
            GameServerGateway gateway
    ) {
        this.expectedPlayers = Set.copyOf(expectedPlayers);
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
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
    public synchronized Set<String> getConnectedPlayers() {
        return Set.copyOf(connectedPlayers);
    }

    @Override
    public synchronized void onServerStart() {
        logger.info("Whitelisted game server ready. Waiting {} seconds for players {}" , connectionTimeoutSeconds, expectedPlayers);
        this.matchStartTask = scheduler.schedule(
                this::startGame,
                connectionTimeoutSeconds,
                TimeUnit.SECONDS
        );
    }

    @Override
    public synchronized void onPlayerConnected(GameSession session) {
        String username = session.getUsername();
        if (!expectedPlayers.contains(username)) {
            logger.warn("Unauthorized player {} tried to connect to match", username);
            return;
        }
        switch (state) {
            case WAITING -> {
                connectedPlayers.add(username);
                logger.info("Player {} joined pre-game lobby ({}/{})", username, connectedPlayers.size(), expectedPlayers.size());
                checkFullCapacityReached();
            }
            case PLAYING -> {
                logger.info("Player {} joined mid-game. Restoring human control", username);
                connectedPlayers.add(username);
                engine.enqueueCommand(new ChangePacmanBehaviourCommand(username, true));
                gateway.sendTcp(username, new GameContextPacket(engine.getLatestContext()));
                gateway.sendTcp(username, new GameStartPacket());
            }
            case FINISHED -> logger.info("Player {} tried to join finished match", username);
        }
    }

    @Override
    public synchronized void onPlayerReconnected(GameSession session) {
        String username = session.getUsername();
        switch (state) {
            case WAITING -> {
                connectedPlayers.add(username);
                logger.info("Player {} reconnected to pre-game lobby ({}/{})", username, connectedPlayers.size(), expectedPlayers.size());
                checkFullCapacityReached();
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

    @Override
    public synchronized void onPlayerDisconnected(GameSession session) {
        String username = session.getUsername();
        switch (state) {
            case WAITING -> {
                connectedPlayers.remove(username);
                logger.info("Player {} left pre-game lobby ({}/{})", username, connectedPlayers.size(), expectedPlayers.size());
            }
            case PLAYING -> {
                connectedPlayers.remove(username);
                logger.info("Player {} disconnected mid-game, substituting with bot", username);
                engine.enqueueCommand(new ChangePacmanBehaviourCommand(username, false));
            }
            case FINISHED -> logger.info("Player {} disconnected after the game has ended", username);
        }
    }

    private void checkFullCapacityReached() {
        if (connectedPlayers.size() == expectedPlayers.size()) {
            if (matchStartTask != null) {
                matchStartTask.cancel(false);
            }
            scheduler.submit(this::startGame);
        }
    }

    private synchronized void startGame() {
        if (state != LobbyState.WAITING) {
            return;
        }
        setState(LobbyState.PLAYING);
        logger.info("Starting game for {}", expectedPlayers);
        for (String player : expectedPlayers) {
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
