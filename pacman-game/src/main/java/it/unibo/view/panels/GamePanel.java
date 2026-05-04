package it.unibo.view.panels;

import it.unibo.controller.commands.PacmanCommand;
import it.unibo.controller.commands.PacmanMoveCommand;
import it.unibo.model.common.Direction;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class GamePanel extends JPanel {
    private Consumer<PacmanCommand> commandSink;
    private Runnable onEscapePressed;

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Panel captured key: " + KeyEvent.getKeyText(e.getKeyCode()));
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> commandSink.accept(new PacmanMoveCommand(null, Direction.UP));
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> commandSink.accept(new PacmanMoveCommand(null, Direction.DOWN));
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> commandSink.accept(new PacmanMoveCommand(null, Direction.LEFT));
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> commandSink.accept(new PacmanMoveCommand(null, Direction.RIGHT));
                    case KeyEvent.VK_ESCAPE -> onEscapePressed.run();
                }
            }
        });
    }

    public void onEscape(Runnable action) {
        this.onEscapePressed = action;
    }

    public void setCommandSink(Consumer<PacmanCommand> sink) {
        this.commandSink = sink;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.drawString("PRESS WASD + ESC TO TEST INPUT", 50, 50);
        g.setColor(Color.YELLOW);
        g.fillOval(375, 275, 50, 50);
    }
}