package it.unibo.view.screens.game;

import it.unibo.controller.client.common.ConnectionParameters;
import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Optional;

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
                Optional<ConnectionParameters> paramsOptional = serviceManager.getGameServerParametersByMatchId();
                if (paramsOptional.isPresent()) {
                    ConnectionParameters parameters = paramsOptional.get();
                    this.lastParameters = parameters;
                    super.startConnection(parameters, serviceManager.getUsername());
                } else {
                    logger.warn("No active game server parameters returned for match ID: {}", serviceManager.getCurrentMatchId());
                    SwingUtilities.invokeLater(() -> screen.showFailureView("Game server is not available for this match. Please try re-queueing."));
                }
            } catch (Exception e) {
                logger.error("Failed to fetch server details from matchmaker", e);
                SwingUtilities.invokeLater(() -> screen.showFailureView("Could not fetch server details"));
            }
        }).start();
    }

    private void handleExitToMainMenu() {
        disconnect();
        new Thread(() -> {
            try {
                // serviceManager.quitMatch();
            } catch (Exception e) {
                logger.warn("Failed to send quitMatch request on exit");
            }
        }).start();
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