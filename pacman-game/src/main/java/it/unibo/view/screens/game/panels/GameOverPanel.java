package it.unibo.view.screens.game.panels;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GameOverPanel extends JPanel {

    private Runnable onGoBackAction;

    public GameOverPanel() {
        setLayout(new GridLayout(3, 1));
        setBackground(Color.BLACK);
        JLabel title = new JLabel("Game Over");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setOpaque(false);
        title.setFont(FontManager.addingFont(50f, FontName.S2P.getFontName()));
        JButton backButton = new JButton("Go Back");
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(Color.WHITE);
        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        backButton.setFont(FontManager.addingFont(40f, FontName.S2P.getFontName()));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(_ -> {
            if (onGoBackAction != null) {
                onGoBackAction.run();
            }
        });
        JPanel buttonPanel = new JPanel(new BorderLayout());
        Border panelBorder = BorderFactory.createEmptyBorder(40, 140, 40, 140);
        buttonPanel.setBorder(panelBorder);
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        add(title);
        JPanel separator = new JPanel();
        separator.setOpaque(false);
        add(separator);
        add(buttonPanel);
    }

    public void setOnGoBack(Runnable action) {
        this.onGoBackAction = action;
    }
}