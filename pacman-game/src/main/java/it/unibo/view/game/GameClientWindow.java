package it.unibo.view.game;

import javax.swing.*;
import java.awt.*;

public class GameClientWindow {
    private final JFrame frame;

    public GameClientWindow() {
        frame = new JFrame("Pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        JPanel rootContainer = new JPanel();
        GameContainer gameContainer = new GameContainer();
        rootContainer.add(gameContainer);
        frame.add(rootContainer);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}