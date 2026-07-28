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

    public void setOnLogin(Runnable action) {
        loginPanel.onClickTopButton(action);
    }

    public void setOnRegister(Runnable action) {
        loginPanel.onClickBottomButton(action);
    }

    public String getUsername() {
        return loginPanel.getUsername();
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
