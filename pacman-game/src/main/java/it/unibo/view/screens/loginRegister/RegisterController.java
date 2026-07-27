package it.unibo.view.screens.loginRegister;

import javax.swing.*;

import it.unibo.controller.client.services.ServiceManager;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

public class RegisterController implements ScreenController {

    private final RegisterView registerView = new RegisterView();

    public RegisterController(AppNavigator navigator, ServiceManager serviceManager) {
        registerView.onRegister(() -> {
            String username = registerView.getUsername();
            String password = registerView.getPassword();
            if (username.isBlank() || password.isBlank()) {
                registerView.showMessage("Please fill in all fields");
                return;
            }
            new Thread(() -> {
                try {
                    String result = serviceManager.register(username, password);
                    SwingUtilities.invokeLater(() -> {
                        registerView.showMessage(result);
                        navigator.goTo(AppState.LOGIN);
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> registerView.showMessage(e.getMessage()));
                }
            }).start();
        });
        registerView.onHome(() -> navigator.goTo(AppState.LOGIN));
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
