package it.unibo.view.screens.loginRegister;

import javax.swing.JPanel;

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
            if (!username.isEmpty() && !password.isEmpty()) {
                try {
                    String result = serviceManager.register(username, password);
                    registerView.showMessage(result);
                    navigator.goTo(AppState.LOGIN);
                } catch (Exception e) {
                    registerView.showMessage(e.getMessage());
                }
            } else {
                registerView.showMessage("Please fill in all fields");
            }
        });
        registerView.onHome(() -> navigator.goTo(AppState.LOGIN));
    }

    @Override
    public JPanel getPanel() {
        return registerView.getPanel();
    }

    @Override
    public void onEnter() {
        // Setup register related stuff
    }

    @Override
    public void onExit() {
        registerView.clearFields();
        // Whatever needs to be done once finished}
    }
}
