package it.unibo.view.screens.matchmaker.panels;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import java.awt.*;

public class MatchmakerSearchingPanel extends JPanel {
    private static final String FONT_NAME = FontName.S2P.getFontName();
    private static final float LABEL_FONT_SIZE = 16f;
    private static final float BUTTON_FONT_SIZE = 14f;
    private static final int THICKNESS = 2;

    private final JLabel statusLabel = new JLabel();
    private final JButton cancelButton = new JButton("Cancel");

    public MatchmakerSearchingPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        statusLabel.setFont(FontManager.addingFont(LABEL_FONT_SIZE, FONT_NAME));
        cancelButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, THICKNESS),
                BorderFactory.createEmptyBorder(15, 25, 15, 25))
        );
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusPanel.add(statusLabel);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        add(statusPanel);
        add(buttonPanel);
    }

    public void updateStatus(String statusText) {
        statusLabel.setText(statusText);
    }

    public void setOnCancel(Runnable action) {
       cancelButton.addActionListener(_ -> action.run());
    }
}