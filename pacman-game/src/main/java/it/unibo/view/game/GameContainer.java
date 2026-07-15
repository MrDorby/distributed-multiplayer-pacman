package it.unibo.view.game;

import it.unibo.controller.client.GameClient;
import it.unibo.controller.client.GameClientFactory;
import it.unibo.controller.client.GameClientListener;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.InputHandlerImpl;
import it.unibo.view.GameView;
import it.unibo.view.HeadlessView;
import it.unibo.view.SwingGameView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class GameContainer extends JPanel implements GameClientListener {
    private static final Logger logger = LoggerFactory.getLogger(GameContainer.class);
    private static final String DEFAULT_USERNAME = "player";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_TCP_PORT = "7777";
    private static final String DEFAULT_UDP_PORT = "7777";

    private final CardLayout cardLayout = new CardLayout();
    private final JLabel connectionStatusLabel = new JLabel("");

    private GameClient client;
    private final GameView gameView;

    private static final String CONNECTION_SETUP = "ConnectionSetup";
    private static final String CONNECTING = "Connecting";
    private static final String GAME = "Game";

    public GameContainer() {
        this.setLayout(cardLayout);
        JPanel connectionSetupPanel = createSetupPanel();
        JPanel connectingPanel = createConnectionPanel();
        this.gameView = new SwingGameView();
        JPanel gamePanel = this.gameView.getGamePanel();
        this.add(connectionSetupPanel, CONNECTION_SETUP);
        this.add(connectingPanel, CONNECTING);
        this.add(gamePanel, GAME);
    }

    private JPanel createSetupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField usernameField = new JTextField(DEFAULT_USERNAME, 15);
        JTextField hostField = new JTextField(DEFAULT_HOST, 15);
        JTextField tcpField = new JTextField(DEFAULT_TCP_PORT, 6);
        JTextField udpField = new JTextField(DEFAULT_UDP_PORT, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Server Host:"), gbc);
        gbc.gridx = 1;
        panel.add(hostField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("TCP Port:"), gbc);
        gbc.gridx = 1;
        panel.add(tcpField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("UDP Port:"), gbc);
        gbc.gridx = 1;
        panel.add(udpField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        JButton connectButton = new JButton("Join Server");
        panel.add(connectButton, gbc);
        connectButton.addActionListener(_ -> {
            String username = usernameField.getText().trim();
            String host = hostField.getText().trim();
            int tcpPort = Integer.parseInt(tcpField.getText().trim());
            int udpPort = Integer.parseInt(udpField.getText().trim());
            updateStatusText("Establishing connection with server...");
            this.cardLayout.show(this, CONNECTING);
            initializeAndConnect(host, tcpPort, udpPort, username);
        });
        return panel;
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(connectionStatusLabel, gbc);
        JButton disconnectButton = new JButton("Cancel");
        gbc.gridy = 1;
        panel.add(disconnectButton, gbc);
        disconnectButton.addActionListener(_ -> disconnect());
        return panel;
    }

    private void initializeAndConnect(String host, int tcpPort, int udpPort, String username) {
        this.client = GameClientFactory.create(host, tcpPort, udpPort, username, new HeadlessView());
        this.client.addListener(this);
        new Thread(() -> {
            try {
                client.start();
                Thread.sleep(1000);
                client.joinGameServer();
                updateStatusText("Connected! Waiting for other players to join...");
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Could not connect to server", "Network Error", JOptionPane.ERROR_MESSAGE);
                    cardLayout.show(this, CONNECTION_SETUP);
                });
            }
        }).start();
    }

    private void disconnect() {
        new Thread(() -> {
            try {
                client.disconnect();
                Thread.sleep(1000);
                client.stop();
            } catch (Exception e) {
                logger.warn("Error while disconnecting client", e);
            } finally {
                SwingUtilities.invokeLater(() -> cardLayout.show(this, CONNECTION_SETUP));
            }
        }).start();
    }

    private void updateStatusText(String text) {
        SwingUtilities.invokeLater(() -> connectionStatusLabel.setText(text));
    }

    @Override
    public void onGameStarted() {
        GameEngine engine = client.getEngine();
        InputHandler inputHandler = new InputHandlerImpl(client.getUsername());
        inputHandler.setEngine(engine);
        gameView.setInputHandler(inputHandler);
        engine.setView(gameView);
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(this, GAME);
            gameView.getGamePanel().requestFocusInWindow();
        });
    }
}