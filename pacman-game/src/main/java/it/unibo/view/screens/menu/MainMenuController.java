package it.unibo.view.screens.menu;

import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppState;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.screens.ScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MainMenuController implements ScreenController {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuController.class);

    private final MainMenuPanel panel = new MainMenuPanel();
    private final AppNavigator navigator;
    private final ServiceManager serviceManager;

    private boolean initialMatchCheckDone = false;

    public MainMenuController(AppNavigator navigator, ServiceManager serviceManager) {
        this.navigator = navigator;
        this.serviceManager = serviceManager;
        panel.setOnPlay(() -> navigator.goTo(AppState.MATCHMAKING));
        panel.setOnStats(() -> navigator.goTo(AppState.STATS));
        panel.setOnLogout(() -> {
            serviceManager.clearMatchmakingData();
            initialMatchCheckDone = false;
            navigator.goTo(AppState.LOGIN);
        });
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onEnter() {
        if (!initialMatchCheckDone) {
            initialMatchCheckDone = true;
            checkForOngoingMatch();
        }
    }

    @Override
    public void onExit() {}

    private void checkForOngoingMatch() {
        logger.info("Checking for active ongoing matches...");
        CompletableFuture.supplyAsync(() -> {
            try {
                return serviceManager.getGameServerParametersByToken();
            } catch (Exception e) {
                logger.warn("Failed to query ongoing match status: {}", e.getMessage(), e);
                return Optional.empty();
            }
        }).thenAcceptAsync(match -> {
            if (match != null && match.isPresent()) {
                logger.info("Ongoing match found for user! Prompting reconnection dialog");
                MainMenuPanel.ReconnectionChoice choice = panel.showReconnectionDialog();
                handleReconnectionChoice(choice);
            } else {
                logger.info("No ongoing match found for user.");
            }
        }, SwingUtilities::invokeLater);
    }

    private void handleReconnectionChoice(MainMenuPanel.ReconnectionChoice choice) {
        if (choice == MainMenuPanel.ReconnectionChoice.RECONNECT) {
            logger.info("Reconnecting user to ongoing match...");
            navigator.goTo(AppState.IN_GAME);
        } else {
            logger.info("User declined reconnection. Leaving match...");
            CompletableFuture.runAsync(() -> {
                try {
                    serviceManager.quitMatch();
                    logger.debug("Notified backend of match being quit");
                } catch (Exception e) {
                    logger.error("Failed to send request to quit match", e);
                }
            });
        }
    }
}