package it.unibo.view;

import it.unibo.controller.commands.PacmanMoveCommand;
import it.unibo.controller.engine.GameEngine;
import it.unibo.model.common.Direction;
import it.unibo.model.game.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameViewImpl implements GameView {
    private static final Logger logger = LoggerFactory.getLogger(GameViewImpl.class);

    // TODO: Propagate the size of the frame from the start view?
    private final static int WIDTH_FRAME = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final static int HEIGHT_FRAME = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final JFrame frame = new JFrame();
    private final GamePanel gamePanel;

    public GameViewImpl(GameEngine engine, GameContext context) {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(new Dimension(WIDTH_FRAME, HEIGHT_FRAME));
        this.frame.setLocationRelativeTo(null);
        this.frame.setFocusable(true);

        this.frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                logger.debug("Key pressed: {}", KeyEvent.getKeyText(e.getKeyCode()));
                // TODO get the UUID of a pacman
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> engine.enqueueCommand(new PacmanMoveCommand(null, Direction.UP));
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> engine.enqueueCommand(new PacmanMoveCommand(null, Direction.DOWN));
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> engine.enqueueCommand(new PacmanMoveCommand(null, Direction.RIGHT));
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT ->engine.enqueueCommand(new PacmanMoveCommand(null, Direction.LEFT));
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        this.gamePanel = new GamePanel(context);
        this.frame.add(this.gamePanel);
    }

    public void show() {
        this.frame.setVisible(true);
    }

    @Override
    public void render(GameContext gameContext) {
        this.gamePanel.setGameContext(gameContext);
        this.gamePanel.repaint();
    }
}
