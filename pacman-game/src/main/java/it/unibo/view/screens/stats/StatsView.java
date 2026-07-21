package it.unibo.view.screens.stats;

import it.unibo.controller.client.common.Stats;

/**
 * StatsView for statistics of the player.
 */
public class StatsView {
    
    private final StatsPanel statsPanel;

    public StatsView(String username) {
        statsPanel = new StatsPanel(username);
    }

    /**
     * @return the StatsPanel.
     */
    public StatsPanel getPanel() {
        return statsPanel;
    }

    /**
     * Adds the player statistics to the differents labels.
     * @param stats Stats to show.
     */
    public void setStats(Stats stats) {
        this.statsPanel.setStats(stats);
    }

    /**
     * Adds an action listener to the home button.
     * @param action to be performed.
     */
    public void onHome(Runnable action) {
        statsPanel.onHomeButton(action);
    }

    /**
     * Shows a Message Dialog to the user.
     * @param message the message that needs to be showed.
     */
    public void showMessage(String message) {
        statsPanel.showMessage(message);
    }
}
