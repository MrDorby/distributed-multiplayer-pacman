package it.unibo.view;

import javax.swing.*;
import java.awt.*;

public class StartView {

    private final static int WIDTH_FRAME = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final static int HEIGHT_FRAME = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final static String FONT_NAME = FontName.S2P.getFontName();
    private final static String TITLE = "PACMAN";
    private final JFrame frame = new JFrame();

    public StartView() {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(new Dimension(WIDTH_FRAME, HEIGHT_FRAME));
        this.frame.setLocationRelativeTo(null);
        this.frame.setFocusable(true);

        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBackground(Color.BLACK);

        JLabel title = new JLabel(TITLE);
        title.setForeground(Color.WHITE);
        title.setFont(FontManager.addingFont(80f, FONT_NAME));
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
        GridBagConstraints titleCons = new GridBagConstraints();
        titleCons.fill = GridBagConstraints.HORIZONTAL;
        titleCons.anchor = GridBagConstraints.PAGE_START;
        titleCons.gridwidth = 3;
        titleCons.gridheight = 1;
        startPanel.add(title, titleCons);

        JPanel loginRegisterPanel = new JPanel();
        loginRegisterPanel.setLayout(new BoxLayout(loginRegisterPanel, BoxLayout.Y_AXIS));
        loginRegisterPanel.setBackground(Color.BLACK);
        loginRegisterPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));
        //login.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        JPanel emailPanel = new JPanel();
        emailPanel.setOpaque(true);
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        emailPanel.add(emailLabel);
        JTextField email = new JTextField();
        email.setFont(new Font(emailLabel.getFont().getFontName(), Font.PLAIN, 13));
        emailPanel.add(email);
        loginRegisterPanel.add(emailPanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setOpaque(true);
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        passwordPanel.add(passwordLabel);
        JPasswordField password = new JPasswordField();
        passwordPanel.add(password);
        loginRegisterPanel.add(passwordPanel);

        JPanel buttonLoginPanel = new JPanel(new BorderLayout());
        JButton signIn = new JButton("Log in");
        buttonLoginPanel.add(signIn);
        loginRegisterPanel.add(buttonLoginPanel);

        JPanel registerPanel = new JPanel(new BorderLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));
        JButton register = new JButton("Register");
        registerPanel.add(register);
        loginRegisterPanel.add(registerPanel);

        GridBagConstraints loginCons = new GridBagConstraints();
        loginCons.fill = GridBagConstraints.BOTH;
        loginCons.gridx = 1;
        loginCons.gridy = 1;
        loginCons.gridwidth = 2;
        loginCons.gridheight = 1;
        loginCons.weighty = 0.1;
        startPanel.add(loginRegisterPanel, loginCons);

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
        startPanel.add(emptyPanel, emptyCons);

        this.frame.add(startPanel);
    }

    public void setVisible() {
        this.frame.setVisible(true);
    }
}
