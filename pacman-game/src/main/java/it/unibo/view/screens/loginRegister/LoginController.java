package it.unibo.view.screens.loginRegister;

import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;

public class LoginController implements ScreenController {
    private final LoginView loginView = new LoginView();

    public LoginController(AppNavigator navigator, ServiceManager serviceManager) {
        loginView.setOnLogin(() -> {
            String username = loginView.getUsername().trim();
            String password = loginView.getPassword();
            if (username.isEmpty() || password.isEmpty()) {
                loginView.showMessage("Please fill in all fields");
                return;
            }
            CompletableFuture.runAsync(() -> {
                try {
                    serviceManager.login(username, password);
                    navigator.goTo(AppState.MAIN_MENU);
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> loginView.showMessage("Login failed"));
                }
            });
        });
        loginView.setOnRegister(() -> navigator.goTo(AppState.REGISTER));
    }
    @Override
    public JPanel getPanel() {
        return loginView.getPanel();
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {
        loginView.clearFields();
    }
}