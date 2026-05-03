package it.unibo.view;

import it.unibo.model.common.GameConstants;
import it.unibo.model.entities.GameEntity;
import it.unibo.model.entities.Pacman;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.TileType;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Objects;

public class GameView {

    private final static int WIDTH_FRAME = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final static int HEIGHT_FRAME = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final JFrame frame = new JFrame();
    private final GamePanel game;

    public GameView(GameContext gameContext) {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(new Dimension(WIDTH_FRAME, HEIGHT_FRAME));
        this.frame.setLocationRelativeTo(null);
        this.frame.setFocusable(true);

        this.frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }
            // TODO: To complete!
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP:
                        break;
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN:
                        break;
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT:
                        break;
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        this.game = new GamePanel(gameContext);
        this.frame.add(this.game);
    }

    public void show() {
        this.frame.setVisible(true);
    }

    public void render(GameContext gameContext) {
        this.game.setGameContext(gameContext);
        this.game.repaint();
    }
}
