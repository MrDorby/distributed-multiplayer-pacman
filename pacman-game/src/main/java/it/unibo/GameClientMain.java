package it.unibo;

import it.unibo.controller.client.GameClient;
import it.unibo.controller.client.GameClientFactory;
import it.unibo.controller.shared.input.PlayerInputHandler;
import it.unibo.view.SwingGameView;

import javax.swing.*;
import java.util.UUID;

public class GameClientMain {
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_TCP_PORT = 7777;
    private static final int DEFAULT_SERVER_UDP_PORT = 7777;

    /**
     * Launches the game client.
     *
     * <p>Usage: {@code GameClientMain [serverHost] [serverTcpPort] [serverUdpPort] [username]}
     */
    static void main(String[] args) throws InterruptedException {
        String host = args.length > 0 ? args[0] : DEFAULT_SERVER_HOST;
        int tcpPort = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_SERVER_TCP_PORT;
        int udpPort = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_SERVER_UDP_PORT;
        String username = args.length > 3 ? args[3] : UUID.randomUUID().toString();

        PlayerInputHandler inputHandler = new PlayerInputHandler(username);
        SwingGameView view = new SwingGameView(inputHandler);
        GameClient client = GameClientFactory.create(host, tcpPort, udpPort, username, view);
        inputHandler.setEngine(client.getEngine());

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pacman Client" + username);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view.getGamePanel());
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            view.getGamePanel().requestFocusInWindow();
        });
        Thread.sleep(1000);
        client.start();
        client.connectToServer();
    }
}