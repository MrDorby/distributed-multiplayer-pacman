package it.unibo.view.screens.game.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

public class ConnectionFailurePanel extends JPanel {
    
    private static final String FONT_NAME = FontName.S2P.getFontName();
    private static final float LABEL_FONT_SIZE = 20f;
    private static final float BUTTON_FONT_SIZE = 18f;
    private static final int THICKNESS = 2;
    private static final Border BUTTON_BORDERS = BorderFactory.createEmptyBorder(20, 20, 20, 20);

    private final JPanel errorPanel = new JPanel();
    private final JPanel backPanel = new JPanel();
    private final JPanel reconnectPanel = new JPanel();

    private final JLabel errorLabel = new JLabel();

    private final JButton backButton = new JButton("Go Back");
    private final JButton reconnectButton = new JButton("Reconnect");

    public ConnectionFailurePanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.YELLOW);
        
        errorLabel.setFont(FontManager.addingFont(LABEL_FONT_SIZE, FONT_NAME));
        errorPanel.setOpaque(false);
        errorPanel.add(errorLabel);
        
        reconnectButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        reconnectButton.setBackground(Color.WHITE);
        reconnectButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, THICKNESS), 
            BUTTON_BORDERS)
        );
        reconnectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reconnectPanel.setOpaque(false);
        reconnectPanel.add(reconnectButton);

        backButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, THICKNESS), 
            BUTTON_BORDERS)
        );
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backPanel.setOpaque(false);
        backPanel.add(backButton);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.add(errorPanel);
        container.add(reconnectPanel);
        container.add(backPanel);

        add(container);

    }

    public void setOnReconnect(Runnable action) {
        reconnectButton.addActionListener(_ -> action.run());
    }

    public void setOnGoBack(Runnable action) {
        backButton.addActionListener(_ -> action.run());
    }

    public void setErrorMessage(String text) {
        errorLabel.setText(text);
    }
}