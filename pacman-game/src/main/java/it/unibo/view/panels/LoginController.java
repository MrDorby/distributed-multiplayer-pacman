package it.unibo.view.panels;

import it.unibo.view.navigation.AppState;
import it.unibo.view.navigation.AppNavigator;

import javax.swing.*;

public class LoginController implements ScreenController {
    private final LoginPanel panel = new LoginPanel();
    private final AppNavigator navigator;

    public LoginController(AppNavigator navigator) {
        this.navigator = navigator;
        panel.onLogin(() -> navigator.goTo(AppState.MAIN_MENU));
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onEnter() {
        // Setup login related stuff
    }

    @Override
    public void onExit() {
        // Whatever needs to be done once finished
    }
}