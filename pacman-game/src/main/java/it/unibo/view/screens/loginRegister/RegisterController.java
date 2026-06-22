package it.unibo.view.screens.loginRegister;

import javax.swing.JPanel;

import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

public class RegisterController implements ScreenController {

    private final RegisterView registerView = new RegisterView();

    public RegisterController(AppNavigator navigator) {
        registerView.onRegister(() -> {
            String email = registerView.getEmail();
            String password = registerView.getPassword();
            if (!email.isEmpty() && !password.isEmpty()) {
                navigator.goTo(AppState.LOGIN);  //TODO: informing the user that the registration went good.
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
