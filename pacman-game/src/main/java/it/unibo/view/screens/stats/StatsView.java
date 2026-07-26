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
    public void onHome(Runnable action) {
        statsPanel.onHomeButton(action);
    }

    /**
     * Shows a Message Dialog to the user.
     * @param message the message that needs to be shown.
     */
    public void showMessage(String message) {
        statsPanel.showMessage(message);
    }

    /**
     * Clears statistic fields back to default state.
     */
    public void clear() {
        this.statsPanel.clearFields();
    }

    /**
     * Sets the view into a loading visual state.
     */
    public void showLoadingState() {
        this.statsPanel.showLoading();
    }
}
