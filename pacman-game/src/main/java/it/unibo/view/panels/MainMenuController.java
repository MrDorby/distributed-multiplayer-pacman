package it.unibo.view.panels;

import it.unibo.view.navigation.AppState;
import it.unibo.view.navigation.AppNavigator;

import javax.swing.*;

public class MainMenuController implements ScreenController {
    private final MainMenuPanel panel = new MainMenuPanel();
    private final AppNavigator navigator;

    public MainMenuController(AppNavigator navigator) {
        this.navigator = navigator;
        panel.onPlay(() -> navigator.goTo(AppState.IN_GAME));
        panel.onStats(() -> navigator.goTo(AppState.STATS));
        panel.onLogout(() -> navigator.goTo(AppState.LOGIN));
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }
}