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

/**
 * Manages the lifecycle of a fresh standard match.
 * <p>
 * Waits for the lobby to fill to capacity before starting the game.
 * Mid-game disconnections substitute human players with AI bots.
 * Mid-game reconnections give control back to the human players.
 */
public class StandardMatchLifecycleManager implements MatchLifecycleManager {
    private static final Logger logger = LoggerFactory.getLogger(StandardMatchLifecycleManager.class);

    private final int capacity;
    private final ServerGameEngine engine;
    private final GameServerGateway gateway;

    private final Set<String> waitingPlayers = new LinkedHashSet<>();
    private final Set<String> activePlayers = new HashSet<>();
    private LobbyState state = LobbyState.WAITING;

    public StandardMatchLifecycleManager(
            int capacity,
            ServerGameEngine engine,
            GameServerGateway gateway
    ) {
        this.capacity = capacity;
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
        return List.copyOf(activePlayers);
    }

    /**
     * Standard matches do not require grace periods or pre-game timers, so this is a no-op.
     */
    @Override
    public synchronized void onServerStart() {}

    /**
     * {@inheritDoc}
     * <p>
     * Registers player in pre-game lobby. Automatically triggers {@link #startGame()}
     * once capacity is reached.
     */
    @Override
    public synchronized void onPlayerConnected(GameSession session) {
        String username = session.getUsername();
        switch (state) {
            case WAITING -> {
                waitingPlayers.add(username);
                logger.info("Player {} joined waiting lobby ({}/{})", username, waitingPlayers.size(), capacity);
                if (waitingPlayers.size() == capacity) {
                    startGame();
                }
            }
            case PLAYING -> logger.info("Player {} tried to join but the game has already started", username);
            case FINISHED -> logger.info("Player {} tried to join but the game has already finished", username);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Re-registers player in lobby if waiting, or restores human control if rejoining mid-game.
     */
    @Override
    public synchronized void onPlayerReconnected(GameSession session) {
        String username = session.getUsername();
        switch (state) {
            case WAITING -> {
                waitingPlayers.add(username);
                logger.info("Player {} reconnected to pre-game lobby ({}/{})", username, waitingPlayers.size(), capacity);
                if (waitingPlayers.size() == capacity) {
                    startGame();
                }
            }
            case PLAYING -> {
                if (!activePlayers.contains(username)) {
                    logger.warn("Unexpected player {} attempted reconnection", username);
                    return;
                }
                logger.info("Player {} reconnected mid-game, restoring human control", username);
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
     * Removes player from the waiting set if waiting, or substitutes them with a bot if mid-game.
     */
    @Override
    public synchronized void onPlayerDisconnected(GameSession session) {
        String username = session.getUsername();
        switch (state) {
            case WAITING -> {
                waitingPlayers.remove(username);
                logger.info("Player {} left pre-game lobby ({}/{})", username, waitingPlayers.size(), capacity);
            }
            case PLAYING -> {
                if (activePlayers.contains(username)) {
                    logger.info("Player {} disconnected mid-game, substituting with a bot", username);
                    engine.enqueueCommand(new ChangePacmanBehaviourCommand(username, false));
                }
            }
            case FINISHED -> logger.info("Player {} disconnected after the game has ended", username);
        }
    }

    private synchronized void startGame() {
        setState(LobbyState.PLAYING);
        activePlayers.addAll(waitingPlayers);
        logger.info("Required player count reached. Starting game with players: {}", activePlayers);
        engine.initialize(new ArrayList<>(activePlayers));
        engine.start();
        gateway.broadcastTcp(new GameContextPacket(engine.getLatestContext()));
        gateway.broadcastTcp(new GameStartPacket());
    }
}
