package it.unibo.view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class StartView {

    private final static int WIDTH_FRAME = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final static int HEIGHT_FRAME = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final static int THICKNESS_BORDER = 2;
    private final static float BUTTON_FONT_SIZE = 14f;
    private final static String FONT_NAME = FontName.S2P.getFontName();
    private final static String TITLE = "PACMAN";
    private final JFrame frame = new JFrame();

    public StartView() {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(new Dimension(WIDTH_FRAME, HEIGHT_FRAME));
        this.frame.setLocationRelativeTo(null);
        this.frame.setFocusable(true);

        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBackground(Color.YELLOW);

        JLabel title = new JLabel(TITLE);
        title.setForeground(Color.BLACK);
        title.setFont(FontManager.addingFont(80f, FONT_NAME));
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));
        GridBagConstraints titleCons = new GridBagConstraints();
        titleCons.fill = GridBagConstraints.HORIZONTAL;
        titleCons.anchor = GridBagConstraints.PAGE_START;
        titleCons.gridwidth = 3;
        titleCons.gridheight = 1;
        startPanel.add(title, titleCons);

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
        JTextField email = new JTextField();
        email.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(0, 10, 0, 10))
        );
        email.setFont(new Font(email.getFont().getFontName(), Font.PLAIN, 15));
        emailTextFieldPanel.add(email);
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
        JPasswordField password = new JPasswordField();
        password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(0, 10, 0, 10))
        );
        password.setFont(new Font(emailLabel.getFont().getFontName(), Font.PLAIN, 15));
        passwordFieldPanel.add(password);
        passwordPanel.add(passwordLabelPanel);
        passwordPanel.add(passwordFieldPanel);
        loginRegisterPanel.add(passwordPanel);

        JPanel buttonLoginPanel = new JPanel(new BorderLayout());
        buttonLoginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton signIn = new JButton("Log in");
        signIn.setBackground(Color.YELLOW);
        signIn.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
        signIn.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        signIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonLoginPanel.add(signIn);
        loginRegisterPanel.add(buttonLoginPanel);

        JPanel registerPanel = new JPanel(new BorderLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        JButton register = new JButton("Register");
        register.setBackground(Color.YELLOW);
        register.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
        register.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        register.setCursor(new Cursor(Cursor.HAND_CURSOR));
        register.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //register.setFont(FontManager.addingFont(BUTTON_FONT_SIZE + 2, FONT_NAME));
                //register.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2 * THICKNESS_BORDER));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //register.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
                //register.setBorder(BorderFactory.createLineBorder(Color.BLACK, THICKNESS_BORDER));
            }
        });
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
