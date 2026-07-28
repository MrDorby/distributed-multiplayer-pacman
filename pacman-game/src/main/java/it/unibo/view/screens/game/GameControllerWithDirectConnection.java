package it.unibo.view.screens.game;

import it.unibo.controller.client.common.ConnectionParameters;
import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Controller for managing direct server connections resolved via matchmaker parameters.
 * <p>
 * Manages the connection lifecycle for direct matches by querying server parameters
 * and handling cached retry attempts up to a maximum threshold.
 * </p>
 */
public class GameControllerWithDirectConnection extends AbstractGameController {
    private static final Logger logger = LoggerFactory.getLogger(GameControllerWithDirectConnection.class);
    private static final int MAX_CACHE_RETRIES = 3;

    private final GameScreenWithDirectConnection screen = new GameScreenWithDirectConnection();
    private final AppNavigator navigator;
    private final ServiceManager serviceManager;

    private int retryCount = 0;
    private ConnectionParameters lastParameters;

    public GameControllerWithDirectConnection(AppNavigator navigator, ServiceManager serviceManager) {
        this.navigator = navigator;
        this.serviceManager = serviceManager;
        this.screen.getConnectingPanel().setOnCancel(this::handleExitToMainMenu);
        this.screen.getGameOverPanel().setOnGoBack(this::handleExitToMainMenu);
        this.screen.getFailurePanel().setOnGoBack(this::handleExitToMainMenu);
        this.screen.getFailurePanel().setOnReconnect(this::handleRetry);
    }

    @Override
    public void onEnter() {
        this.retryCount = 0;
        this.lastParameters = null;
        fetchFreshServerDetailsAndConnect();
    }

    private void handleRetry() {
        if (retryCount < MAX_CACHE_RETRIES && lastParameters != null) {
            retryCount++;
            logger.info("Retrying connection with cached parameters (Attempt {}/{})", retryCount, MAX_CACHE_RETRIES);
            super.startConnection(lastParameters, serviceManager.getUsername());
        } else {
            logger.info("Max cached retries reached. Re-querying matchmaker for fresh server details...");
            retryCount = 0;
            fetchFreshServerDetailsAndConnect();
        }
    }

    private void fetchFreshServerDetailsAndConnect() {
        screen.showConnectingView("Fetching server details...");
        new Thread(() -> {
            try {
                String user = serviceManager.getUsername();
                ConnectionParameters parameters = serviceManager.getGameServerParameters();
                this.lastParameters = parameters;
                super.startConnection(parameters, user);
            } catch (Exception e) {
                logger.error("Failed to fetch server details from matchmaker", e);
                SwingUtilities.invokeLater(() -> screen.showFailureView("Could not fetch server details: " + e.getMessage()));
            }
        }).start();
    }

    private void handleExitToMainMenu() {
        disconnect();
        if (navigator != null) {
            navigator.goTo(AppState.MAIN_MENU);
        }
    }

    @Override
    protected void handleClose() {
        this.retryCount = 0;
        this.lastParameters = null;
        handleExitToMainMenu();
    }

    @Override
    protected AbstractGameScreen getGameScreen() {
        return screen;
    }
}