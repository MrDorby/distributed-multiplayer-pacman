package it.unibo.view.screens.matchmaker;

import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class MatchmakerController implements ScreenController {
    private static final Logger logger = LoggerFactory.getLogger(MatchmakerController.class);

    private final MatchmakerScreen screen;
    private final AppNavigator navigator;
    private final ServiceManager serviceManager;

    private final AtomicBoolean searching = new AtomicBoolean(false);
    private CompletableFuture<Void> matchmakingFuture;

    public MatchmakerController(AppNavigator navigator, ServiceManager serviceManager) {
        this.navigator = navigator;
        this.serviceManager = serviceManager;
        this.screen = new MatchmakerScreen(List.of("map1", "map2", "map3"));
        this.screen.getMenuPanel().setOnQueue(this::handleStartQueue);
        this.screen.getMenuPanel().setOnGoBack(this::handleReturnToMainMenu);
        this.screen.getSearchingPanel().setOnCancel(this::cancelQueue);
        this.screen.getFailurePanel().setOnOk(this::handleReturnToMainMenu);
    }

    private void handleStartQueue() {
        startQueue(screen.getMenuPanel().getSelectedMap());
    }

    private void startQueue(String selectedMap) {
        searching.set(true);
        logger.info("Starting matchmaking for map: {}", selectedMap);
        SwingUtilities.invokeLater(screen::showSearchingView);
        matchmakingFuture = CompletableFuture.runAsync(() -> {
            try {
                updateSearchingStatus("Queueing for " + selectedMap + "...");
                if (!serviceManager.queue(selectedMap)) {
                    logger.warn("Failed to join queue for map: {}", selectedMap);
                    showFailureView("Failed to join queue. Please try again.");
                    return;
                }
                pollQueueStatus();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                if (searching.get()) {
                    logger.error("Matchmaking failed unexpectedly", e);
                    showFailureView("Network Error during queueing");
                }
            }
        });
    }

    private void pollQueueStatus() throws Exception {
        boolean matchFound = (serviceManager.getCurrentMatchId() != null);
        while (searching.get() && !matchFound) {
            updateSearchingStatus("Searching for players...");
            matchFound = serviceManager.checkQueueStatus();
        }
        searching.set(false);
        logger.info("Match found! Match ID: {}", serviceManager.getCurrentMatchId());
        updateSearchingStatus("Match found!");
        Thread.sleep(500);
        SwingUtilities.invokeLater(() -> navigator.goTo(AppState.IN_GAME));
    }

    private void cancelQueue() {
        if (searching.get()) {
            logger.info("Matchmaking cancelled by user.");
        }
        stopMatchmaking();
        CompletableFuture.runAsync(() -> {
            try {
                serviceManager.cancelQueue();
            } catch (Exception e) {
                logger.warn("Error while attempting to cancel queue on backend", e);
            } finally {
                serviceManager.clearMatchmakingData();
            }
        });
        SwingUtilities.invokeLater(screen::showMenuView);
    }

    private void stopMatchmaking() {
        searching.set(false);
        if (matchmakingFuture != null && !matchmakingFuture.isDone()) {
            matchmakingFuture.cancel(true);
            matchmakingFuture = null;
        }
    }

    private void handleReturnToMainMenu() {
        navigator.goTo(AppState.MAIN_MENU);
    }

    private void updateSearchingStatus(String message) {
        SwingUtilities.invokeLater(() -> screen.getSearchingPanel().updateStatus(message));
    }

    private void showFailureView(String errorMessage) {
        SwingUtilities.invokeLater(() -> screen.showFailureView(errorMessage));
    }

    @Override
    public JPanel getPanel() {
        return screen;
    }

    @Override
    public void onEnter() {
        this.screen.showMenuView();
    }

    @Override
    public void onExit() {
        cancelQueue();
    }
}