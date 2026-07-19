package it.unibo.view;

import it.unibo.controller.shared.input.InputHandler;
import it.unibo.model.common.Direction;
import it.unibo.model.game.GameContext;
import it.unibo.view.screens.game.panels.GamePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SwingGameView implements GameView {
    private static final Logger logger = LoggerFactory.getLogger(SwingGameView.class);
    private final GamePanel gamePanel;
    private InputHandler inputHandler;

    public SwingGameView() {
        this.gamePanel = new GamePanel();
        this.gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (inputHandler == null) {
                    return;
                }
                logger.debug("Key pressed: {}", KeyEvent.getKeyText(e.getKeyCode()));
                Direction targetDirection = null;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> targetDirection = Direction.UP;
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> targetDirection = Direction.DOWN;
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> targetDirection = Direction.RIGHT;
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> targetDirection = Direction.LEFT;
                }
                if (targetDirection != null) {
                    inputHandler.onDirectionPressed(targetDirection);
                }
            }
        });
    }

    @Override
    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public JPanel getGamePanel() {
        return this.gamePanel;
    }

    @Override
    public void render(GameContext gameContext) {
        this.gamePanel.setGameContext(gameContext);
    }
}
