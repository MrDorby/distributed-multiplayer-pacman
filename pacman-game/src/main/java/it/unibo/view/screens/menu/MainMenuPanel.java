package it.unibo.view.screens.menu;

import javax.swing.*;

class MainMenuPanel extends JPanel {
    private final JButton playButton = new JButton("Find Match");
    private final JButton statsButton = new JButton("My Stats");
    private final JButton logoutButton = new JButton("Logout");

    public MainMenuPanel() {
        this.add(playButton);
        this.add(statsButton);
        this.add(logoutButton);
    }

    public void onPlay(Runnable action) {
        playButton.addActionListener(e -> action.run());
    }

    public void onStats(Runnable action) {
        statsButton.addActionListener(e -> action.run());
    }

    public void onLogout(Runnable action) {
        logoutButton.addActionListener(e -> action.run());
    }
}