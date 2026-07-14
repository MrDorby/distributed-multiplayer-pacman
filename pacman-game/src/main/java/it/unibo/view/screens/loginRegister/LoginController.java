package it.unibo.view.screens.loginRegister;

import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;

public class LoginController implements ScreenController {
    private final LoginView loginView = new LoginView();

    public LoginController(AppNavigator navigator, ServiceManager serviceManager) {
        loginView.onLogin(() -> {
            String username = loginView.getUsername();
            String password = loginView.getPassword();
            if (!username.isEmpty() && !password.isEmpty()) {
                try {
                    serviceManager.login(username, password); // TODO: TO CHECK THE MAIN MENU, COMMENT THIS LINE.
                    navigator.goTo(AppState.MAIN_MENU);
                } catch (Exception e) {
                    loginView.showMessage(e.getMessage());
                }
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