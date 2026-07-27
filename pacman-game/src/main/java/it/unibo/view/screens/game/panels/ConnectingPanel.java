package it.unibo.view.screens.game.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

public class ConnectingPanel extends JPanel {
    
    private static final String FONT_NAME = FontName.S2P.getFontName();
    private static final float LABEL_FONT_SIZE = 16f;
    private static final float BUTTON_FONT_SIZE = 14f;
    private static final int THICKNESS = 2;

    private final JPanel statusPanel = new JPanel();
    private final JPanel buttonPanel = new JPanel();

    private final JLabel statusLabel = new JLabel();

    private final JButton cancelButton = new JButton("Cancel");

    public ConnectingPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.YELLOW);
        
        statusLabel.setFont(FontManager.addingFont(LABEL_FONT_SIZE, FONT_NAME));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusPanel.add(statusLabel);

        cancelButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, THICKNESS), 
            BorderFactory.createEmptyBorder(20, 30, 20, 30))
        );
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.add(statusPanel);
        container.add(buttonPanel);

        add(container);
    }

    public void setOnCancel(Runnable action) {
        cancelButton.addActionListener(_ -> action.run());
    }

    public void updateStatus(String text) {
        statusLabel.setText(text);
    }
}