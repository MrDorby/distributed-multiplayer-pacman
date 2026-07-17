package it.unibo.view.game;

import javax.swing.*;
import java.awt.*;

public class GameClientFrame {
    private final JFrame frame;

    public GameClientFrame() {
        frame = new JFrame("Pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        GameContainer gameContainer = new GameContainer();
        GameController controller = new GameController(gameContainer);
        frame.add(gameContainer, BorderLayout.CENTER);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}