package it.unibo;

import it.unibo.controller.client.GameClientController;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.PlayerInputHandler;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameViewImpl;

import javax.swing.*;
import java.util.UUID;

public class GameClientMain {
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_TCP_PORT = 7777;
    private static final int DEFAULT_SERVER_UDP_PORT = 7778;

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
        Game game = new GameImpl(null);
        GameClientController controller = new GameClientController(game, host, tcpPort, udpPort, username);
        GameEngine engine = controller.getEngine();
        InputHandler inputHandler = new PlayerInputHandler(engine, username);
        GameViewImpl view = new GameViewImpl(inputHandler);
        engine.setView(view);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pacman Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view.getGamePanel());
            frame.setSize(800, 600);
            frame.setVisible(true);
            view.getGamePanel().requestFocusInWindow();
        });
    }
}