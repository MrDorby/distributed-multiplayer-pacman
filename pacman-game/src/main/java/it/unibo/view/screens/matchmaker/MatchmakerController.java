package it.unibo.view.screens.matchmaker;

import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;
import java.util.List;

public class MatchmakerController implements ScreenController {
    private final MatchmakerScreen screen;
    private final AppNavigator navigator;
    private final ServiceManager serviceManager;

    private Thread matchmakingThread;
    private volatile boolean searching = false;

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
        String selectedMap = screen.getMenuPanel().getSelectedMap();
        startQueue(selectedMap);
    }

    private void startQueue(String selectedMap) {
        searching = true;
        SwingUtilities.invokeLater(screen::showSearchingView);
        matchmakingThread = new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> screen.getSearchingPanel().updateStatus("Queueing for " + selectedMap + "..."));
                boolean joinedSuccessfully = serviceManager.queue(selectedMap);
                if (!joinedSuccessfully) {
                    SwingUtilities.invokeLater(() -> screen.showFailureView("Failed to join queue."));
                    return;
                }
                boolean ready = (serviceManager.getCurrentMatchId() != null);
                while (searching && !ready) {
                    SwingUtilities.invokeLater(() -> screen.getSearchingPanel().updateStatus("Searching for players..."));
                    ready = serviceManager.checkQueueStatus();
                }
                if (!searching) return;
                SwingUtilities.invokeLater(() -> screen.getSearchingPanel().updateStatus("Match found!"));
                Thread.sleep(500);
                SwingUtilities.invokeLater(() -> navigator.goTo(AppState.IN_GAME));
            } catch (Exception e) {
                if (searching) {
                    SwingUtilities.invokeLater(() -> screen.showFailureView("Network Error: " + e.getMessage()));
                }
            }
        });
        matchmakingThread.start();
    }

    private void cancelQueue() {
        stopMatchmakingThread();
        new Thread(() -> {
            try {
                serviceManager.cancelQueue();
            } catch (Exception _) {
            } finally {
                serviceManager.clearMatchmakingData();
            }
        }).start();
        SwingUtilities.invokeLater(screen::showMenuView);
    }

    private void stopMatchmakingThread() {
        searching = false;
        if (matchmakingThread != null && matchmakingThread.isAlive()) {
            matchmakingThread.interrupt();
            matchmakingThread = null;
        }
    }

    private void handleReturnToMainMenu() {
        navigator.goTo(AppState.MAIN_MENU);
    }

    @Override
    public JPanel getPanel() {
        return screen;
    }

    @Override
    public void onEnter() {
        SwingUtilities.invokeLater(screen::showMenuView);
    }

    @Override
    public void onExit() {
        stopMatchmakingThread();
        new Thread(() -> {
            try {
                serviceManager.cancelQueue();
            } catch (Exception _) {
            } finally {
                serviceManager.clearMatchmakingData();
            }
        }).start();
    }
}