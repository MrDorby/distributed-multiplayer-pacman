package it.unibo.view.screens.stats;

import javax.swing.JPanel;

import it.unibo.controller.client.common.Stats;
import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

/**
 * StatsController for the player statistics.
 */
public class StatsController implements ScreenController {

    private final StatsView statsView;

    public StatsController(AppNavigator navigator, ServiceManager serviceManager) {
        this.statsView = new StatsView(serviceManager.getUsername());
        try {
            Stats stats = serviceManager.getPlayerInfo();
            this.statsView.setStats(stats);
        } catch (Exception e) {
            this.statsView.setStats(null);
            statsView.showMessage(e.getMessage());
        }
        statsView.onHome(() -> navigator.goTo(AppState.MAIN_MENU));
    }

    @Override
    public JPanel getPanel() {
        return statsView.getPanel();
    }

    @Override
    public void onEnter() {
        // Setup Stats related stuff.
    }

    @Override
    public void onExit() {
        // Whatever needs to be done once finished.
    }
    
}
