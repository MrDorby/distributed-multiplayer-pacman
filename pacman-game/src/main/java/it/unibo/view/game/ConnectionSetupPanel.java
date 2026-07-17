package it.unibo.view.game;

import javax.swing.*;
import java.awt.*;

public class ConnectionSetupPanel extends JPanel {
    private final JTextField userField = new JTextField("player", 15);
    private final JTextField hostField = new JTextField("localhost", 15);
    private final JTextField tcpField = new JTextField("7777", 6);
    private final JTextField udpField = new JTextField("7777", 6);
    private final JButton connectButton = new JButton("Join Server");

    public ConnectionSetupPanel(GameContainer.ViewListener listener) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addGrid(this, new JLabel("Username:"), gbc, 0, 0);
        addGrid(this, userField, gbc, 1, 0);
        addGrid(this, new JLabel("Server Host:"), gbc, 0, 1);
        addGrid(this, hostField, gbc, 1, 1);
        addGrid(this, new JLabel("TCP Port:"), gbc, 0, 2);
        addGrid(this, tcpField, gbc, 1, 2);
        addGrid(this, new JLabel("UDP Port:"), gbc, 0, 3);
        addGrid(this, udpField, gbc, 1, 3);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(connectButton, gbc);

        connectButton.addActionListener(_ -> {
            if (listener != null) {
                try {
                    listener.onConnectRequested(
                            userField.getText().trim(),
                            hostField.getText().trim(),
                            Integer.parseInt(tcpField.getText().trim()),
                            Integer.parseInt(udpField.getText().trim())
                    );
                } catch (NumberFormatException ex) {
                }
            }
        });
    }

    private void addGrid(Container c, Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        c.add(comp, gbc);
    }
}