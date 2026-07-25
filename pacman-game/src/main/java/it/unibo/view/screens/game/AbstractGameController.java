package it.unibo.view.screens.game;

import it.unibo.controller.client.GameClient;
import it.unibo.controller.client.GameClientFactory;
import it.unibo.controller.client.GameClientListener;
import it.unibo.controller.client.dto.ConnectionParameters;
import it.unibo.controller.client.network.sockets.session.ConnectionState;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.InputHandlerImpl;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.view.GameView;
import it.unibo.view.screens.ScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Abstract base controller managing network client lifecycles and game view transitions.
 * <p>
 * Implements {@link GameClientListener} to react to network state changes, driving the active
 * game screen through connection, active gameplay, and game-over states.
 */
public abstract class AbstractGameController implements GameClientListener, ScreenController {
    private static final Logger logger = LoggerFactory.getLogger(AbstractGameController.class);

    private GameClient client;

    protected void startConnection(ConnectionParameters parameters, String user) {
        disconnect();
        GameView gameView = getGameScreen().getGameView();
        this.client = GameClientFactory.create(parameters.host(), parameters.tcpPort(), parameters.udpPort(), user, gameView);
        this.client.addListener(this);
        new Thread(() -> {
            try {
                client.start();
                Thread.sleep(500);
                client.joinServer();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> getGameScreen().showFailureView("Network Error: " + e.getMessage()));
            }
        }).start();
    }

    protected void disconnect() {
        if (client == null) return;
        try {
            client.stop();
        } catch (Exception e) {
            logger.debug("Error during client shutdown", e);
        } finally {
            client = null;
        }
    }

    /* ************************************ *
     * GameClient Interface Event Hooks
     * ************************************ */

    @Override
    public void onGameStarted() {
        GameEngine engine = client.getEngine();
        InputHandler inputHandler = new InputHandlerImpl(client.getUsername());
        inputHandler.setEngine(engine);
        GameView gameView = getGameScreen().getGameView();
        gameView.getGamePanel().setLocalPlayerId(client.getUsername());
        gameView.setInputHandler(inputHandler);
        engine.setView(gameView);
        engine.start();
        SwingUtilities.invokeLater(getGameScreen()::showGameView);
    }

    @Override
    public void onGameEnded(GameContextDTO context) {
        disconnect();
        SwingUtilities.invokeLater(() -> {
            getGameScreen().getGameOverPanel().updateStats(context);
            getGameScreen().showGameOverView();
        });
    }

    @Override
    public void onConnectionStateChanged(ConnectionState state) {
        SwingUtilities.invokeLater(() -> {
            switch (state) {
                case CONNECTING   -> getGameScreen().showConnectingView("Connecting to server...");
                case HANDSHAKING  -> getGameScreen().showConnectingView("Syncing with server...");
                case CONNECTED    -> getGameScreen().showConnectingView("Connected. Waiting for others to join...");
                case FAILED       -> {
                    disconnect();
                    getGameScreen().showFailureView("Could not establish connection with server");
                }
                case LOST         -> {
                    disconnect();
                    getGameScreen().showFailureView("Connection to the server lost abruptly");
                }
            }
        });
    }

    protected abstract AbstractGameScreen getGameScreen();

    @Override
    public JPanel getPanel() {
        return getGameScreen();
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {
        disconnect();
    }
}