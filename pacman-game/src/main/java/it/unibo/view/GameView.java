package it.unibo.view;

import it.unibo.model.entities.Pacman;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class GameView {

    private final static int WIDTH_FRAME = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final static int HEIGHT_FRAME = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final JFrame frame = new JFrame();
    private final GameMapView game;

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
        this.game = new GameMapView(gameContext);
        this.frame.add(this.game);
    }

    private static class GameMapView extends JPanel {

        private GameContext gameContext;
        private JPanel scoreboard;
        private JPanel map;
        private JPanel menu;
        private JPanel life;

        private GameMapView(GameContext gameContext) {
            this.gameContext = gameContext;
            this.setBackground(Color.BLACK);
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();

            this.map = new JPanel();
            map.setBackground(Color.YELLOW);
            //constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            //constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.weightx = 0.8;
            constraints.weighty = 0.7;
            this.add(map, constraints);

            scoreboardPanel();
            constraints.fill = GridBagConstraints.BOTH;
            //constraints.anchor = GridBagConstraints.LINE_END;
            constraints.gridx = 2;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.weightx = 0.2;
            constraints.weighty = 0.7;
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scoreboard, BorderLayout.NORTH);
            this.add(panel, constraints);

            this.menu = new JPanel();
            menu.setBackground(Color.CYAN);
            //constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            //constraints.anchor = GridBagConstraints.PAGE_START;
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 3;
            constraints.weighty = 0.05;
            this.add(menu, constraints);

            this.life = new JPanel();
            life.setBackground(Color.GREEN);
            //constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            //constraints.anchor = GridBagConstraints.PAGE_START;
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 3;
            constraints.weighty = 0.08;
            this.add(life, constraints);
        }

        private void scoreboardPanel() {
            this.scoreboard = new JPanel(new GridLayout(0,2));
            this.scoreboard.setBackground(Color.RED);
            this.scoreboard.setBorder(new LineBorder(Color.BLACK, 2));
            //this.scoreboard.setOpaque(false);
            this.scoreboard.add(new JLabel("Player name", SwingConstants.CENTER));
            this.scoreboard.add(new JLabel("Scores", SwingConstants.CENTER));
            //this.gameContext.getGameState().getLeaderboard().forEach(this::setScoreboardInfos);
            Arrays.stream(this.scoreboard.getComponents())
                    .filter(x -> x instanceof JLabel)
                    .forEach(x -> x.setForeground(Color.WHITE));
        }

        private void setScoreboardInfos(Pacman player, Integer score) {
            this.scoreboard.add(new JLabel(player.getId().toString()));
            this.scoreboard.add(new JLabel(String.valueOf(score)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawOval(50, 50, 20, 20);
            scoreboardPanel();
        }

        protected void setGameContext(GameContext gameContext) {
            this.gameContext = Objects.requireNonNull(gameContext);
        }
    }

    public void show() {
        this.frame.setVisible(true);
    }

    public void render(GameContext gameContext) {
        this.game.setGameContext(gameContext);
        this.game.repaint();
    }
}
