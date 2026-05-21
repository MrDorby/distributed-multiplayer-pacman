package it.unibo.view.screens.login;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends JPanel {
    private final static int THICKNESS_BORDER = 2;
    private final static float BUTTON_FONT_SIZE = 14f;
    private final static String FONT_NAME = FontName.S2P.getFontName();
    private final static String TITLE = "PACMAN";

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton signInButton = new JButton("Log in");
    private final JButton registerButton = new JButton("Register");

    public LoginPanel() {
        this.setBackground(Color.YELLOW);
        this.setLayout(new GridBagLayout());

        JLabel title = new JLabel(TITLE);
        title.setForeground(Color.BLACK);
        title.setFont(FontManager.addingFont(80f, FONT_NAME));
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));
        GridBagConstraints titleCons = new GridBagConstraints();
        titleCons.fill = GridBagConstraints.HORIZONTAL;
        titleCons.anchor = GridBagConstraints.PAGE_START;
        titleCons.gridwidth = 3;
        titleCons.gridheight = 1;
        this.add(title, titleCons);

        JPanel loginRegisterPanel = new JPanel();
        loginRegisterPanel.setLayout(new BoxLayout(loginRegisterPanel, BoxLayout.Y_AXIS));
        loginRegisterPanel.setBorder(BorderFactory.createMatteBorder(THICKNESS_BORDER + 2, 0, THICKNESS_BORDER + 2, 0, Color.BLACK));
        //login.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(FontManager.addingFont(15f, FONT_NAME));
        emailLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        emailLabelPanel.add(emailLabel);
        JPanel emailTextFieldPanel = new JPanel(new BorderLayout());
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(0, 10, 0, 10))
        );
        emailField.setFont(new Font(emailField.getFont().getFontName(), Font.PLAIN, 15));
        emailTextFieldPanel.add(emailField);
        emailPanel.add(emailLabelPanel);
        emailPanel.add(emailTextFieldPanel);
        loginRegisterPanel.add(emailPanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setOpaque(true);
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel passwordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(FontManager.addingFont(15f, FONT_NAME));
        passwordLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        passwordLabelPanel.add(passwordLabel);
        JPanel passwordFieldPanel = new JPanel(new BorderLayout());
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(0, 10, 0, 10))
        );
        passwordField.setFont(new Font(emailLabel.getFont().getFontName(), Font.PLAIN, 15));
        passwordFieldPanel.add(passwordField);
        passwordPanel.add(passwordLabelPanel);
        passwordPanel.add(passwordFieldPanel);
        loginRegisterPanel.add(passwordPanel);

        JPanel buttonLoginPanel = new JPanel(new BorderLayout());
        buttonLoginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        signInButton.setBackground(Color.YELLOW);
        signInButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
        signInButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        signInButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonLoginPanel.add(signInButton);
        loginRegisterPanel.add(buttonLoginPanel);

        JPanel registerPanel = new JPanel(new BorderLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        registerButton.setBackground(Color.YELLOW);
        registerButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
        registerButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE + 2, FONT_NAME));
                registerButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2 * THICKNESS_BORDER));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
                registerButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
            }
        });
        registerPanel.add(registerButton);
        loginRegisterPanel.add(registerPanel);

        GridBagConstraints loginCons = new GridBagConstraints();
        loginCons.fill = GridBagConstraints.BOTH;
        loginCons.gridx = 1;
        loginCons.gridy = 1;
        loginCons.gridwidth = 2;
        loginCons.gridheight = 1;
        loginCons.weighty = 0.1;
        this.add(loginRegisterPanel, loginCons);

        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        GridBagConstraints emptyCons = new GridBagConstraints();
        emptyCons.fill = GridBagConstraints.BOTH;
        emptyCons.anchor = GridBagConstraints.PAGE_END;
        emptyCons.gridx = 1;
        emptyCons.gridy = 2;
        emptyCons.gridwidth = 2;
        emptyCons.gridheight = 1;
        emptyCons.weighty = 0.1;
        this.add(emptyPanel, emptyCons);
    }

    public void onLogin(Runnable action) {
        signInButton.addActionListener(e -> action.run());
    }

    public void onRegister(Runnable action) {
        registerButton.addActionListener(e -> action.run());
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void clearFields() {
        this.emailField.setText("");
        this.passwordField.setText("");
    }
}
