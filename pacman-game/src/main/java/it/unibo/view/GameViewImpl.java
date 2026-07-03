package it.unibo.view;

import it.unibo.controller.shared.input.InputHandler;
import it.unibo.model.common.Direction;
import it.unibo.model.game.GameContext;
import it.unibo.view.screens.game.GameOverPanel;
import it.unibo.view.screens.game.GamePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameViewImpl implements GameView {
    private static final Logger logger = LoggerFactory.getLogger(GameViewImpl.class);
    private final GamePanel gamePanel;

    public GameViewImpl(InputHandler inputHandler) {
        this.gamePanel = new GamePanel();
        this.gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
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

    public JPanel getGamePanel() {
        return this.gamePanel;
    }

    @Override
    public void render(GameContext gameContext) {
        this.gamePanel.setGameContext(gameContext);
    }

    @Override
    public void show() {
        this.gamePanel.setVisible(true);
    }

    @Override
    public void displayWinView(Runnable onExit) {
        new GameOverPanel(onExit);
    }

    @Override
    public void displayGameOverView() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'displayGameOverView'");
    }
}
