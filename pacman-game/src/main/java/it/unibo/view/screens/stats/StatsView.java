package it.unibo.view.screens.stats;

import it.unibo.controller.client.common.PlayerStats;

/**
 * StatsView for statistics of the player.
 */
public class StatsView {
    
    private final StatsPanel statsPanel;

    public StatsView() {
        statsPanel = new StatsPanel();
    }

    /**
     * @return the StatsPanel.
     */
    public StatsPanel getPanel() {
        return statsPanel;
    }

    /**
     * Sets the username to be displayed
     * @param username the username to be displayed
     */
    public void setUsername(String username) {
        this.statsPanel.setUsername(username);
    }

    /**
     * Adds the player statistics to the different labels.
     * @param stats Stats to show.
     */
    public void setStats(PlayerStats stats) {
        this.statsPanel.setStats(stats);
    }

    /**
     * Adds an action listener to the home button.
     * @param action to be performed.
     */
    public void setOnHome(Runnable action) {
        statsPanel.setOnHome(action);
    }

    /**
     * Shows a Message Dialog to the user.
     * @param message the message that needs to be shown.
     */
    public void showMessage(String message) {
        statsPanel.showMessage(message);
    }

    /**
     * Sets the view into a loading visual state.
     */
    public void showLoading() {
        this.statsPanel.showLoading();
    }
}
