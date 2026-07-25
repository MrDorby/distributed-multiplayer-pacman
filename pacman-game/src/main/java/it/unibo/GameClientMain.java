package it.unibo;

import it.unibo.view.screens.game.GameControllerWithManualConnection;

import javax.swing.*;
import java.awt.*;

public class GameClientMain {

    private final JFrame frame;

    public GameClientMain() {
        frame = new JFrame("Pacman Standalone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
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