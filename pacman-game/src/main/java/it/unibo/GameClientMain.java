package it.unibo;

import it.unibo.view.screens.game.GameControllerWithManualConnection;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the standalone Pacman client.
 * <p>
 * Unlike {@link FullGameClientMain}, this entry point bypasses authentication
 * and navigation menus, launching directly into the game view with a manual server address prompt.
 * </p>
 */
public class GameClientMain {

    private final JFrame frame = new JFrame("Pacman Standalone");

    public GameClientMain() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1280, 720));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        GameControllerWithManualConnection controller = new GameControllerWithManualConnection();
        frame.add(controller.getPanel(), BorderLayout.CENTER);
        controller.onEnter();
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    static void main() {
        SwingUtilities.invokeLater(() -> {
            GameClientMain app = new GameClientMain();
            app.show();
        });
    }
}