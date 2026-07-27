package it.unibo.view.screens.menu;

import it.unibo.view.navigation.AppState;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;

public class MainMenuController implements ScreenController {
    private final MainMenuPanel panel = new MainMenuPanel();

    public MainMenuController(AppNavigator navigator) {
        panel.onPlay(() -> navigator.goTo(AppState.MATCHMAKING));
        panel.onStats(() -> navigator.goTo(AppState.STATS));
        panel.onLogout(() -> navigator.goTo(AppState.LOGIN));
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {}
}