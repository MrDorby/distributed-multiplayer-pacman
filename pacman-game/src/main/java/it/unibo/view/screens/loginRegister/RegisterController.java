package it.unibo.view.screens.loginRegister;

import javax.swing.*;

import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import java.util.concurrent.CompletableFuture;

public class RegisterController implements ScreenController {

    private final RegisterView registerView = new RegisterView();

    public RegisterController(AppNavigator navigator, ServiceManager serviceManager) {
        registerView.setOnRegister(() -> {
            String username = registerView.getUsername();
            String password = registerView.getPassword();
            if (username.isBlank() || password.isBlank()) {
                registerView.showMessage("Please fill in all fields");
                return;
            }
            CompletableFuture.runAsync(() -> {
                try {
                    String result = serviceManager.register(username, password);
                    SwingUtilities.invokeLater(() -> {
                        registerView.showMessage(result);
                        navigator.goTo(AppState.LOGIN);
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> registerView.showMessage("Registration failed"));
                }
            });
        });
        registerView.setOnHome(() -> navigator.goTo(AppState.LOGIN));
    }

    @Override
    public JPanel getPanel() {
        return registerView.getPanel();
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {
        registerView.clearFields();
    }
}
