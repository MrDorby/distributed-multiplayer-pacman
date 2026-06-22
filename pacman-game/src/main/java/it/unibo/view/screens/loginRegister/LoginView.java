package it.unibo.view.screens.loginRegister;

public class LoginView {
    
    private final static String LOGIN_TEXT = "Login";
    private final static String REGISTER_TEXT = "Register";
    private final LoginRegisterPanel loginPanel;

    public LoginView() {
        loginPanel = new LoginRegisterPanel(LOGIN_TEXT, LOGIN_TEXT, REGISTER_TEXT);
    }

    public LoginRegisterPanel getPanel() {
        return loginPanel;
    }

    public void onLogin(Runnable action) {
        loginPanel.onClickTopButton(action);
    }

    public void onRegister(Runnable action) {
        loginPanel.onClickBottomButton(action);
    }

    public String getEmail() {
        return loginPanel.getEmail();
    }

    public String getPassword() {
        return loginPanel.getPassword();
    }

    public void clearFields() {
        loginPanel.clearFields();
    }

    public void showMessage(String message) {
        loginPanel.showMessage(message);
    }
}
