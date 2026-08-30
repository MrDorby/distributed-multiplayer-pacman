package it.unibo.view.screens.game;

import it.unibo.controller.client.common.ConnectionParameters;
import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for managing direct server connections resolved via matchmaker parameters.
 * <p>
 * Queries the matchmaker for active game server parameters on join and retry attempts.
 */
public class GameControllerWithDirectConnection extends AbstractGameController {
    private static final Logger logger = LoggerFactory.getLogger(GameControllerWithDirectConnection.class);

    private final GameScreenWithDirectConnection screen = new GameScreenWithDirectConnection();
    private final AppNavigator navigator;
    private final ServiceManager serviceManager;

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
        fetchServerParametersAndConnect();
    }

    private void handleRetry() {
        logger.info("Retrying connection. Querying matchmaker for fresh server details...");
        fetchServerParametersAndConnect();
    }

    private void fetchServerParametersAndConnect() {
        screen.showConnectingView("Fetching server parameters...");
        CompletableFuture.runAsync(() -> {
            try {
                // Try fetching by match ID
                Optional<ConnectionParameters> parameters = serviceManager.getGameServerParametersByMatchId();
                // If local matchId was lost/null, query backend by session token
                if (parameters.isEmpty()) {
                    logger.info("Match ID parameter lookup returned empty. Attempting recovery via user token...");
                    parameters = serviceManager.getGameServerParametersByToken(false);
                }
                if (parameters.isPresent()) {
                    logger.info("Player {} is connected to GameServer with parameters: {}", serviceManager.getUsername(), parameters.get());
                    super.startConnection(parameters.get(), serviceManager.getUsername());
                } else {
                    logger.warn("No game server parameters were found for match ID: {}", serviceManager.getCurrentMatchId());
                    SwingUtilities.invokeLater(() -> screen.showFailureView("Game server is not available"));
                }
            } catch (Exception e) {
                logger.error("Failed to fetch server parameters from matchmaker", e);
                SwingUtilities.invokeLater(() -> screen.showFailureView("Could not fetch server parameters"));
            }
        });
    }

    @Override
    protected void handleClose() {
        handleExitToMainMenu();
    }

    @Override
    protected AbstractGameScreen getGameScreen() {
        return screen;
    }

    private void handleExitToMainMenu() {
        super.disconnect();
        if (navigator != null) {
            navigator.goTo(AppState.MAIN_MENU);
        }
    }
}