package it.unibo.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class GameView {

    private final static int WIDTH_FRAME = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final static int HEIGHT_FRAME = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel();

    public GameView() {
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
        this.panel.setBackground(Color.BLACK);
        this.panel.setLayout(new BorderLayout());
        JPanel scoreboard = new JPanel(new GridLayout(0,2));
        scoreboard.setOpaque(false);
        scoreboard.add(new JLabel("Player name", SwingConstants.CENTER));
        scoreboard.add(new JLabel("Scores", SwingConstants.CENTER));
        Arrays.stream(scoreboard.getComponents())
                .filter(x -> x instanceof JLabel)
                .forEach(x -> x.setForeground(Color.WHITE));
        this.panel.add(scoreboard, BorderLayout.NORTH);
        JPanel map = new JPanel();
        this.panel.add(map);
        this.frame.add(this.panel);

    }

    public void show() {
        this.frame.setVisible(true);
    }
}
