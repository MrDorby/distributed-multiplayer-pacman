package it.unibo.view;

import it.unibo.controller.shared.engine.GameLifecycleEvent;
import it.unibo.controller.shared.engine.GameEndedEvent;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.model.common.Direction;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameState;
import it.unibo.view.screens.game.GamePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

public class SwingGameView implements GameView {
    private static final Logger logger = LoggerFactory.getLogger(SwingGameView.class);
    private final GamePanel gamePanel;

    public SwingGameView(InputHandler inputHandler) {
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
    public void onLifecycleEvent(GameLifecycleEvent event) {
        if (event instanceof GameEndedEvent(GameContext context)) {
            String winner = context.getGameState().getWinnerId();
            JOptionPane.showMessageDialog(gamePanel, "The winner is " + winner, "Match Results", JOptionPane.PLAIN_MESSAGE);
        }
    }
}
