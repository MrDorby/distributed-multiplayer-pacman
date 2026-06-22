package it.unibo.view.screens.loginRegister;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class LoginRegisterPanel extends JPanel {
    private static final int MIN_PANEL_SIZE = 800;
    private final static int THICKNESS_BORDER = 2;
    private final static float BUTTON_FONT_SIZE = 14f;
    private final static String FONT_NAME = FontName.S2P.getFontName();
    private final static int maxBorder = 20;
    private final static int minBorder = 10;

    private final JPanel loginRegisterPanel = new JPanel();
    private final JPanel emailPanel = new JPanel();
    private final JPanel passwordPanel = new JPanel();
    private final JPanel buttonClickTopButtonPanel = new JPanel(new BorderLayout());
    private final JPanel registerPanel = new JPanel(new BorderLayout());
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton topButton = new JButton();
    private final JButton bottomButton = new JButton();
    private Border panelBorder = BorderFactory.createEmptyBorder(maxBorder, maxBorder, maxBorder, maxBorder);

    public LoginRegisterPanel(String textTitle, String textTop, String textBottom) {
        this.setBackground(Color.YELLOW);
        this.setLayout(new GridBagLayout());
        this.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (getHeight() < MIN_PANEL_SIZE) {
                    emailPanel.setBorder(BorderFactory.createEmptyBorder(minBorder, minBorder, minBorder, minBorder));
                    passwordPanel.setBorder(BorderFactory.createEmptyBorder(minBorder, minBorder, minBorder, minBorder));
                    buttonClickTopButtonPanel.setBorder(BorderFactory.createEmptyBorder(minBorder, minBorder, minBorder, minBorder));
                    registerPanel.setBorder(BorderFactory.createEmptyBorder(minBorder, minBorder, minBorder, minBorder));
                } else {
                    emailPanel.setBorder(panelBorder);
                    passwordPanel.setBorder(panelBorder);
                    buttonClickTopButtonPanel.setBorder(panelBorder);
                    registerPanel.setBorder(panelBorder);
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'componentMoved'");
            }

            @Override
            public void componentShown(ComponentEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'componentShown'");
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'componentHidden'");
            }
            
        });

        JLabel title = new JLabel(textTitle);
        title.setForeground(Color.BLACK);
        title.setFont(FontManager.addingFont(80f, FONT_NAME));
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));
        GridBagConstraints titleCons = new GridBagConstraints();
        titleCons.fill = GridBagConstraints.HORIZONTAL;
        titleCons.anchor = GridBagConstraints.PAGE_START;
        titleCons.gridwidth = 3;
        titleCons.gridheight = 1;
        this.add(title, titleCons);

        loginRegisterPanel.setLayout(new BoxLayout(loginRegisterPanel, BoxLayout.Y_AXIS));
        loginRegisterPanel.setBorder(BorderFactory.createMatteBorder(THICKNESS_BORDER + 2, 0, THICKNESS_BORDER + 2, 0, Color.BLACK));
        //login.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBorder(panelBorder);
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

        passwordPanel.setOpaque(true);
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBorder(panelBorder);
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

        buttonClickTopButtonPanel.setBorder(panelBorder);
        topButton.setText(textTop);
        topButton.setBackground(Color.YELLOW);
        topButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
        topButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        topButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonClickTopButtonPanel.add(topButton);
        loginRegisterPanel.add(buttonClickTopButtonPanel);

        registerPanel.setBorder(panelBorder);
        bottomButton.setText(textBottom);
        bottomButton.setBackground(Color.YELLOW);
        bottomButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
        bottomButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        bottomButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        /*registerButton.addMouseListener(new MouseAdapter() {
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
        });*/
        registerPanel.add(bottomButton);
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

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void onClickTopButton(Runnable action) {
        topButton.addActionListener(e -> action.run());
    }

    public void onClickBottomButton(Runnable action) {
        bottomButton.addActionListener(e -> action.run());
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
