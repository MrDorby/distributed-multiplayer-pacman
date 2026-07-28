package it.unibo.view.screens.game;

import it.unibo.controller.client.common.ConnectionParameters;
import it.unibo.view.screens.game.panels.ConnectionSetupPanel;

import javax.swing.*;

/**
 * Controller for manually configured direct connections (custom host and ports).
 * <p>
 * Manages the UI lifecycle of {@link GameScreenWithManualConnection} and caches user-entered
 * connection parameters for instant retries without requiring re-entry.
 * </p>
 */
public class GameControllerWithManualConnection extends AbstractGameController {
    private static final String DEFAULT_USERNAME = "player";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_TCP_PORT = 7777;
    private static final int DEFAULT_UDP_PORT = 7777;

    private final GameScreenWithManualConnection screen = new GameScreenWithManualConnection();
    private String lastUsername = DEFAULT_USERNAME;
    private ConnectionParameters lastParameters = new ConnectionParameters(DEFAULT_HOST, DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);

    public GameControllerWithManualConnection() {
        this.screen.getConnectionSetupPanel().setOnConnect(this::handleConnect);
        this.screen.getConnectingPanel().setOnCancel(this::handleExitToSetup);
        this.screen.getGameOverPanel().setOnGoBack(this::handleExitToSetup);
        this.screen.getFailurePanel().setOnGoBack(this::handleExitToSetup);
        this.screen.getFailurePanel().setOnReconnect(this::handleRetry);
    }

    @Override
    public void onEnter() {
        this.screen.getConnectionSetupPanel().setConnectionFields(
                DEFAULT_USERNAME,
                DEFAULT_HOST,
                DEFAULT_TCP_PORT,
                DEFAULT_UDP_PORT
        );
        this.screen.showConnectionSetupView();
    }

    private void handleConnect() {
        try {
            ConnectionSetupPanel panel = screen.getConnectionSetupPanel();
            this.lastUsername = panel.getUsername();
            String host = panel.getHost();
            int tcp = Integer.parseInt(panel.getTcpText());
            int udp = Integer.parseInt(panel.getUdpText());
            this.lastParameters = new ConnectionParameters(host, tcp, udp);
            super.startConnection(this.lastParameters, this.lastUsername);
        } catch (NumberFormatException e) {
            screen.showFailureView("Bad connection parameters");
        }
    }

    private void handleRetry() {
        if (lastParameters != null) {
            super.startConnection(lastParameters, lastUsername);
        }
    }

    private void handleExitToSetup() {
        super.disconnect();
        SwingUtilities.invokeLater(screen::showConnectionSetupView);
    }

    @Override
    protected void handleClose() {
        handleExitToSetup();
    }

    @Override
    protected AbstractGameScreen getGameScreen() {
        return screen;
    }
}