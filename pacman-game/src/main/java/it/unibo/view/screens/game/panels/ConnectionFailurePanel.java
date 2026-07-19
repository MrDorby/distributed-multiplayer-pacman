package it.unibo.view.screens.game.panels;

import javax.swing.*;
import java.awt.*;

public class ConnectionFailurePanel extends JPanel {
    private final JLabel errorLabel = new JLabel();

    private Runnable onReconnectAction;
    private Runnable onGoBackAction;

    public ConnectionFailurePanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.YELLOW);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(errorLabel, gbc);
        gbc.gridy = 1;
        JButton reconnectButton = new JButton("Reconnect");
        add(reconnectButton, gbc);
        gbc.gridy = 2;
        JButton backButton = new JButton("Go Back");
        add(backButton, gbc);
        reconnectButton.addActionListener(_ -> {
            if (onReconnectAction != null) {
                onReconnectAction.run();
            }
        });
        backButton.addActionListener(_ -> {
            if (onGoBackAction != null) {
                onGoBackAction.run();
            }
        });
    }

    public void setOnReconnect(Runnable action) {
        this.onReconnectAction = action;
    }

    public void setOnGoBack(Runnable action) {
        this.onGoBackAction = action;
    }

    public void setErrorMessage(String text) {
        errorLabel.setText(text);
    }
}