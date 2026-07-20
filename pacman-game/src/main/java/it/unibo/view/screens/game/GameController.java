package it.unibo.view.screens.game;

import it.unibo.controller.client.GameClient;
import it.unibo.controller.client.GameClientFactory;
import it.unibo.controller.client.GameClientListener;
import it.unibo.controller.client.network.sockets.session.ConnectionState;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.InputHandlerImpl;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.view.GameView;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;
import it.unibo.view.screens.game.panels.ConnectionSetupPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class GameController implements GameClientListener, ScreenController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameScreen screen;
    private final AppNavigator navigator;

    private final DummyMatchmakerClient matchmakerClient = new DummyMatchmakerClient();
    private final DummyAuthClient authClient = new DummyAuthClient();

    private GameClient client;

    private String lastUser;
    private String lastHost;
    private int lastTcp;
    private int lastUdp;

    public GameController(AppNavigator navigator) {
        this.navigator = navigator;
        this.screen = new GameScreen();

        this.screen.getSetupPanel().setOnConnect(this::handleConnectRequest);
        this.screen.getConnectingPanel().setOnCancel(this::handleExit);
        this.screen.getGameOverPanel().setOnGoBack(this::handleExit);
        this.screen.getFailurePanel().setOnGoBack(this::handleExit);
        this.screen.getFailurePanel().setOnReconnect(this::retryLastConnection);
    }

    private void handleConnectRequest() {
        try {
            ConnectionSetupPanel setupPanel = screen.getSetupPanel();
            String user = setupPanel.getUsername();
            String host = setupPanel.getHost();
            int tcp = Integer.parseInt(setupPanel.getTcpText());
            int udp = Integer.parseInt(setupPanel.getUdpText());
            cacheParams(user, host, tcp, udp);
            startConnection(host, tcp, udp, user);
        } catch (NumberFormatException e) {
            this.screen.showFailureView("Bad parameters");
        }
    }

    private void cacheParams(String user, String host, int tcp, int udp) {
        this.lastUser = user;
        this.lastHost = host;
        this.lastTcp = tcp;
        this.lastUdp = udp;
    }

    private void handleExit() {
        new Thread(() -> {
            cleanup();
            SwingUtilities.invokeLater(screen::showConnectionSetupView);
            if (navigator != null) {
                navigator.goTo(AppState.MAIN_MENU);
            }
        }).start();
    }

    private void startConnection(String host, int tcp, int udp, String user) {
        cleanup();
        this.client = GameClientFactory.create(host, tcp, udp, user, screen.getGameView());
        this.client.addListener(this);
        new Thread(() -> {
            try {
                client.start();
                Thread.sleep(500);
                client.joinServer();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> screen.showFailureView("Network Error: " + e.getMessage()));
            }
        }).start();
    }

    public void retryLastConnection() {
        startConnection(lastHost, lastTcp, lastUdp, lastUser);
    }

    private void cleanup() {
        if (client == null) return;
        try {
            client.disconnect();
            client.stop();
        } catch (Exception e) {
            logger.debug("Error encountered during client shutdown");
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
        GameView gameView = screen.getGameView();
        gameView.getGamePanel().setLocalPlayerId(client.getUsername());
        gameView.setInputHandler(inputHandler);
        engine.setView(gameView);
        engine.start();
        SwingUtilities.invokeLater(screen::showGameView);
    }

    @Override
    public void onGameEnded(GameContextDTO context) {
        cleanup();
        SwingUtilities.invokeLater(() -> {
            screen.getGameOverPanel().updateStats(context);
            screen.showGameOverView();
        });
    }

    @Override
    public void onConnectionStateChanged(ConnectionState state) {
        SwingUtilities.invokeLater(() -> {
            switch (state) {
                case CONNECTING   -> screen.showConnectingView("Connecting to server...");
                case HANDSHAKING  -> screen.showConnectingView("Syncing with server...");
                case CONNECTED    -> screen.showConnectingView("Connected. Waiting for others to join...");
                case FAILED       -> handleNetworkIssue("Could not establish connection with server");
                case LOST         -> handleNetworkIssue("Connection to the server lost abruptly");
            }
        });
    }

    private void handleNetworkIssue(String message) {
        cleanup();
        SwingUtilities.invokeLater(() -> screen.showFailureView(message));
    }

    @Override
    public JPanel getPanel() {
        return this.screen;
    }

    @Override
    public void onEnter() {
        String user = authClient.getUsername();
        String host = matchmakerClient.getHost();
        int tcp = matchmakerClient.getTcpPort();
        int udp = matchmakerClient.getUdpPort();
        cacheParams(user, host, tcp, udp);
        SwingUtilities.invokeLater(() -> {
            this.screen.getSetupPanel().setConnectionFields(user, host, tcp, udp);
            this.screen.showConnectionSetupView();
        });
    }

    @Override
    public void onExit() {
        cleanup();
    }
}