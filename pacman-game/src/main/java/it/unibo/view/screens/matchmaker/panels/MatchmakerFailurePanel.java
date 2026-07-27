package it.unibo.view.screens.matchmaker.panels;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import java.awt.*;

public class MatchmakerFailurePanel extends JPanel {
    private static final String FONT_NAME = FontName.S2P.getFontName();
    private static final float LABEL_FONT_SIZE = 16f;
    private static final float BUTTON_FONT_SIZE = 14f;
    private static final int THICKNESS = 2;

    private final JLabel errorLabel = new JLabel();
    private final JButton okButton = new JButton("OK");

    public MatchmakerFailurePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        errorLabel.setFont(FontManager.addingFont(LABEL_FONT_SIZE, FONT_NAME));
        okButton.setFont(FontManager.addingFont(BUTTON_FONT_SIZE, FONT_NAME));
        okButton.setBackground(Color.WHITE);
        okButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, THICKNESS),
                BorderFactory.createEmptyBorder(15, 25, 15, 25))
        );
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel labelPanel = new JPanel();
        labelPanel.setOpaque(false);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        labelPanel.add(errorLabel);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        add(labelPanel);
        add(buttonPanel);
    }

    public void setErrorText(String text) {
        errorLabel.setText(text);
    }

    public void setOnOk(Runnable action) {
        okButton.addActionListener(_ -> action.run());
    }
}