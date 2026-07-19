package it.unibo.view.screens.game;

import javax.swing.*;
import java.awt.*;

public class SimpleGameClientGui {
    private final JFrame frame;

    public SimpleGameClientGui() {
        frame = new JFrame("Pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        GameController controller = new GameController(null);
        frame.add(controller.getPanel(), BorderLayout.CENTER);
        controller.onEnter();
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}