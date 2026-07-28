package it.unibo.view.screens.stats;

import javax.swing.*;

import it.unibo.controller.client.common.PlayerStats;
import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import java.util.concurrent.CompletableFuture;

/**
 * StatsController for the player statistics.
 */
public class StatsController implements ScreenController {

    private final StatsView statsView;
    private final ServiceManager serviceManager;

    public StatsController(AppNavigator navigator, ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.statsView = new StatsView();
        this.statsView.setOnHome(() -> navigator.goTo(AppState.MAIN_MENU));
    }

    @Override
    public JPanel getPanel() {
        return statsView.getPanel();
    }

    @Override
    public void onEnter() {
        statsView.showLoading();
        CompletableFuture.runAsync(() -> {
            try {
                String username = serviceManager.getUsername();
                PlayerStats stats = serviceManager.getPlayerInfo();
                SwingUtilities.invokeLater(() -> {
                    statsView.setUsername(username);
                    statsView.setStats(stats);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statsView.setStats(null);
                    statsView.showMessage("Failed to load stats");
                });
            }
        });
    }

    @Override
    public void onExit() {}
}
