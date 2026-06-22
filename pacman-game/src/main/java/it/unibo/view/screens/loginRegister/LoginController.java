package it.unibo.view.screens.loginRegister;

import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;

public class LoginController implements ScreenController {
    private final LoginView loginView = new LoginView();

    public LoginController(AppNavigator navigator) {
        loginView.onLogin(() -> {
            String email = loginView.getEmail();
            String password = loginView.getPassword();
            if (!email.isEmpty() && !password.isEmpty()) {
                navigator.goTo(AppState.MAIN_MENU);
            } else {
                loginView.showMessage("Please fill in all fields");
            }
        });
        loginView.onRegister(() -> navigator.goTo(AppState.REGISTER));
    }

    @Override
    public JPanel getPanel() {
        return loginView.getPanel();
    }

    @Override
    public void onEnter() {
        // Setup login related stuff
    }

    @Override
    public void onExit() {
        loginView.clearFields();
        // Whatever needs to be done once finished
    }
}