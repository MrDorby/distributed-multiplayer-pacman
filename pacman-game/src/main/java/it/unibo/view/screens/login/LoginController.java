package it.unibo.view.screens.login;

import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;

public class LoginController implements ScreenController {
    private final LoginPanel loginPanel = new LoginPanel();

    public LoginController(AppNavigator navigator) {
        loginPanel.onLogin(() -> {
            String email = loginPanel.getEmail();
            String password = loginPanel.getPassword();
            if (!email.isEmpty() && !password.isEmpty()) {
                navigator.goTo(AppState.MAIN_MENU);
            } else {
                JOptionPane.showMessageDialog(loginPanel, "Please fill in all fields");
            }
        });
        loginPanel.onRegister(() -> navigator.goTo(AppState.REGISTER));
    }

    @Override
    public JPanel getPanel() {
        return loginPanel;
    }

    @Override
    public void onEnter() {
        // Setup login related stuff
    }

    @Override
    public void onExit() {
        loginPanel.clearFields();
        // Whatever needs to be done once finished
    }
}