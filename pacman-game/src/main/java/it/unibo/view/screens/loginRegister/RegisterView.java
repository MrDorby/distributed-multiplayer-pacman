package it.unibo.view.screens.loginRegister;

public class RegisterView {

    private final static String REGISTER_TEXT = "Register";
    private final static String HOME_TEXT = "Home";
    private final LoginRegisterPanel registerPanel;

    public RegisterView() {
        registerPanel = new LoginRegisterPanel(REGISTER_TEXT, REGISTER_TEXT, HOME_TEXT);
    }

    public LoginRegisterPanel getPanel() {
        return registerPanel;
    }

    public void onRegister(Runnable action) {
        registerPanel.onClickTopButton(action);
    }

    public void onHome(Runnable action) {
        registerPanel.onClickBottomButton(action);
    }

    public String getUsername() {
        return registerPanel.getUsername();
    }

    public String getPassword() {
        return registerPanel.getPassword();
    }

    public void clearFields() {
        registerPanel.clearFields();
    }

    public void showMessage(String message) {
        registerPanel.showMessage(message);
    }
}
