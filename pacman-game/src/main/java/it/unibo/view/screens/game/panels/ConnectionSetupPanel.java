package it.unibo.view.screens.game.panels;

import javax.swing.*;
import java.awt.*;

public class ConnectionSetupPanel extends JPanel {
    private final JTextField userField = new JTextField("", 15);
    private final JTextField hostField = new JTextField("", 15);
    private final JTextField tcpField = new JTextField("", 6);
    private final JTextField udpField = new JTextField("", 6);

    private Runnable onConnectAction;

    public ConnectionSetupPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.YELLOW);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Server Host:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(hostField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("TCP Port:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(tcpField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("UDP Port:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(udpField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton connectButton = new JButton("Join Server");
        add(connectButton, gbc);

        connectButton.addActionListener(_ -> {
            if (onConnectAction != null) {
                onConnectAction.run();
            }
        });
    }

    public String getUsername() {
        return userField.getText().trim();
    }

    public String getHost() {
        return hostField.getText().trim();
    }

    public String getTcpText() {
        return tcpField.getText().trim();
    }

    public String getUdpText() {
        return udpField.getText().trim();
    }

    public void setConnectionFields(String username, String host, int tcp, int udp) {
        userField.setText(username);
        hostField.setText(host);
        tcpField.setText(String.valueOf(tcp));
        udpField.setText(String.valueOf(udp));
    }

    public void setOnConnect(Runnable action) {
        this.onConnectAction = action;
    }
}