package it.unibo.controller.client;

import it.unibo.controller.client.engine.ClientGameEngine;
import it.unibo.controller.client.network.sockets.GameClientGateway;
import it.unibo.controller.client.network.sockets.session.ConnectionState;
import it.unibo.controller.client.network.sockets.session.ClientSessionListener;
import it.unibo.controller.client.network.sockets.session.ClientGameSessionManager;
import it.unibo.controller.shared.engine.event.GameEndedEvent;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.engine.command.PacmanCommand;
import it.unibo.controller.shared.engine.command.PacmanMoveCommand;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.sockets.packets.PacmanMovePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameClientImpl implements GameClient, ClientSessionListener {
    private static final Logger logger = LoggerFactory.getLogger(GameClientImpl.class);

    private final GameClientGateway gateway;
    private final ClientGameSessionManager sessionManager;
    private final ClientGameEngine engine;

    private final List<GameClientListener> listeners = new CopyOnWriteArrayList<>();

    public GameClientImpl(
            ClientGameEngine engine, GameClientGateway gateway,
            ClientGameSessionManager sessionManager) {
        this.engine = engine;
        this.gateway = gateway;
        this.sessionManager = sessionManager;
    }

    @Override
    public void addListener(GameClientListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void start() {
        try {
            gateway.start();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to start game client", e);
        }
    }

    @Override
    public void stop() {
        sessionManager.disconnect();
        sessionManager.close();
        engine.stop();
        gateway.stop();
    }

    /* ************************************ *
     * Session lifecycle methods
     * ************************************ */

    @Override
    public void joinServer() {
        sessionManager.joinServer();
    }

    @Override
    public String getUsername() {
        return sessionManager.getUsername();
    }

    @Override
    public void onConnectionStateChanged(ConnectionState state) {
        for (GameClientListener listener : listeners) {
            listener.onConnectionStateChanged(state);
        }
    }

    /* ************************************ *
     * Game client network listener methods *
     * ************************************ */

    @Override
    public void onGameContext(GameContextDTO context) {
        logger.trace("Received authoritative game context update");
        engine.onGameContextUpdate(context);
    }

    @Override
    public void onGameStart() {
        logger.debug("Received signal to start the game. Starting the engine");
        for (GameClientListener listener : listeners) {
            listener.onGameStarted();
        }
    }

    @Override
    public void onGameEnd(GameContextDTO context) {
        logger.debug("Received game end event from server");
        engine.onGameEvent(new GameEndedEvent(context));
        for (GameClientListener listener : listeners) {
            listener.onGameEnded(context);
        }
    }

    /* ************************************ *
     * Game engine command listener methods *
     * ************************************ */

    @Override
    public void onGameCommand(PacmanCommand command) {
        if (command instanceof PacmanMoveCommand moveCommand) {
            logger.debug("Sending move command: {}", moveCommand);
            gateway.sendUdp(new PacmanMovePacket(moveCommand.pacmanId(), moveCommand.direction()));
        }
    }

    /* *********************** *
     * Client specific methods *
     * *********************** */

    @Override
    public GameEngine getEngine() {
        return engine;
    }
}
