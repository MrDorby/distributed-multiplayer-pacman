package it.unibo.view.game;

import it.unibo.controller.client.GameClient;
import it.unibo.controller.client.GameClientFactory;
import it.unibo.controller.client.GameClientListener;
import it.unibo.controller.client.network.sockets.session.ConnectionState;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.InputHandlerImpl;
import it.unibo.view.HeadlessView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class GameController implements GameContainer.ViewListener, GameClientListener {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameContainer view;
    private GameClient client;

    public GameController(GameContainer view) {
        this.view = view;
        this.view.setListener(this);
    }

    @Override
    public void onConnectRequested(String username, String host, int tcp, int udp) {
        view.showConnectingStatusView("Establishing connection with server...");
        startConnectionWorker(host, tcp, udp, username);
    }

    @Override
    public void onCancelRequested() {
        new Thread(() -> {
            cleanupActiveClient();
            SwingUtilities.invokeLater(view::showConnectionSetupView);
        }).start();
    }

    private void startConnectionWorker(String host, int tcp, int udp, String user) {
        cleanupActiveClient();
        this.client = GameClientFactory.create(host, tcp, udp, user, new HeadlessView());
        this.client.addListener(this);
        new Thread(() -> {
            try {
                client.start();
                Thread.sleep(500);
                client.joinServer();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    view.updateStatusPanel("Network Error: " + e.getMessage(), true);
                });
            }
        }).start();
    }

    private void cleanupActiveClient() {
        if (client == null) return;
        try {
            client.disconnect();
            client.stop();
        } catch (Exception e) {
        } finally {
            client = null;
        }
    }

    @Override
    public void onGameStarted() {
        GameEngine engine = client.getEngine();
        InputHandler inputHandler = new InputHandlerImpl(client.getUsername());
        inputHandler.setEngine(engine);
        view.getGameView().setInputHandler(inputHandler);
        engine.setView(view.getGameView());
        SwingUtilities.invokeLater(view::showGameView);
    }

    @Override
    public void onConnectionStateChanged(ConnectionState state) {
        SwingUtilities.invokeLater(() -> {
            switch (state) {
                case CONNECTING   -> view.updateStatusPanel("Connecting to server...", false);
                case HANDSHAKING  -> view.updateStatusPanel("Establishing connection", false);
                case CONNECTED    -> view.updateStatusPanel("Connected! Waiting for others to join...", false);
                case DISCONNECTED -> view.updateStatusPanel("Disconnected from server", false);
                case FAILED       -> handleFailureState();
                case LOST         -> handleAbruptDisconnect();
            }
        });
    }

    private void handleFailureState() {
        cleanupActiveClient();
        view.updateStatusPanel("Connection Failure: Could not establish a link.", true);
    }

    private void handleAbruptDisconnect() {
        cleanupActiveClient();
        view.updateStatusPanel("Connection lost abruptly to the server.", true);
    }
}