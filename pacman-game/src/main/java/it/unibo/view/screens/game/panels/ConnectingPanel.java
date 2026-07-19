package it.unibo.view.screens.game.panels;

import javax.swing.*;
import java.awt.*;

public class ConnectingPanel extends JPanel {
    private final JLabel statusLabel = new JLabel();

    private Runnable onCancelAction;

    public ConnectingPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.YELLOW);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(statusLabel, gbc);
        gbc.gridy = 1;
        JButton cancelButton = new JButton("Cancel");
        add(cancelButton, gbc);
        cancelButton.addActionListener(_ -> {
            if (onCancelAction != null) {
                onCancelAction.run();
            }
        });
    }

    public void setOnCancel(Runnable action) {
        this.onCancelAction = action;
    }

    public void updateStatus(String text) {
        statusLabel.setText(text);
    }
}