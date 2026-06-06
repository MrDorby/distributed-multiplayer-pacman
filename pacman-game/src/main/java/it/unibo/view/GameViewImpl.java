package it.unibo.view;

import it.unibo.controller.commands.PacmanMoveCommand;
import it.unibo.controller.engine.GameEngine;
import it.unibo.model.common.Direction;
import it.unibo.model.game.GameContext;
import it.unibo.view.screens.game.GamePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameViewImpl implements GameView {
    private static final Logger logger = LoggerFactory.getLogger(GameViewImpl.class);
    private final GamePanel gamePanel;

    public GameViewImpl(GameEngine engine, GameContext context) {
        this.gamePanel = new GamePanel(context);
        this.gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                logger.debug("Key pressed: {}", KeyEvent.getKeyText(e.getKeyCode()));
                // TODO get the username of a pacman
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> engine.enqueueCommand(new PacmanMoveCommand(null, Direction.UP));
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> engine.enqueueCommand(new PacmanMoveCommand(null, Direction.DOWN));
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> engine.enqueueCommand(new PacmanMoveCommand(null, Direction.RIGHT));
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT ->engine.enqueueCommand(new PacmanMoveCommand(null, Direction.LEFT));
                }
            }
        });
    }

    public JPanel getGamePanel() {
        return this.gamePanel;
    }

    @Override
    public void render(GameContext gameContext) {
        this.gamePanel.setGameContext(gameContext);
        this.gamePanel.repaint();
    }

    @Override
    public void show() {
        this.gamePanel.setVisible(true);
    }
}
