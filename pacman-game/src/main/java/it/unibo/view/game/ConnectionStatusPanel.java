package it.unibo.view.game;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConnectionStatusPanel extends JPanel {
    private final JLabel statusLabel = new JLabel("");
    private final JButton actionButton = new JButton("Cancel");

    public ConnectionStatusPanel(ActionListener cancelAction) {
        setLayout(new GridBagLayout());
        setBackground(Color.YELLOW);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        Font customFont = FontManager.addingFont(16.0f, FontName.S2P.getFontName());
        statusLabel.setFont(customFont);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 20, 20);
        add(statusLabel, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 0, 20);
        actionButton.addActionListener(cancelAction);
        add(actionButton, gbc);
    }

    public void updateStatus(String text, boolean isError) {
        statusLabel.setText(text);
        if (isError) {
            actionButton.setText("Back to Main Menu");
        } else {
            actionButton.setText("Cancel");
        }
        revalidate();
        repaint();
    }
}