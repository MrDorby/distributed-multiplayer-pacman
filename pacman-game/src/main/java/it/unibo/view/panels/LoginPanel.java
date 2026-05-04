package it.unibo.view.panels;

import javax.swing.*;

class LoginPanel extends JPanel {
    private final JTextField userField = new JTextField(15);
    private final JButton loginBtn = new JButton("Login");

    public LoginPanel() {
        this.add(new JLabel("Username:"));
        this.add(userField);
        this.add(loginBtn);
    }

    public void onLogin(Runnable action) {
        loginBtn.addActionListener(e -> action.run());
    }

    public String getUsername() {
        return userField.getText();
    }

    public void clear() {
        userField.setText("");
    }
}