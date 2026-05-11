package it.unibo.view;

import it.unibo.model.common.GameConstants;
import it.unibo.model.entities.Pacman;
import it.unibo.model.game.GameContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;import java.util.Arrays;
import java.util.Objects;

public class GamePanel extends JPanel {

    private final static int PLAYER_NAME_LENGTH = 12;
    private final static String FONT_NAME = FontName.S2P.getFontName();
    private GameContext gameContext;
    private JPanel scoreboard;
    private JPanel mapContainer;
    private JPanel gameMapView;
    private JPanel menu;
    private JPanel life;

    public GamePanel(GameContext gameContext) {
        this.gameContext = gameContext;
        this.setBackground(Color.BLACK);
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // Creates the panel for the map of the game.
        this.mapContainer = new JPanel(new GridBagLayout());
        //mapContainer.setBackground(Color.YELLOW);
        mapContainer.setBackground(Color.GREEN);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 0.9;
        constraints.weighty = 0.2;
        this.gameMapView = new GameMapPanel(gameContext);
        int size = (int) Math.sqrt(gameContext.getMap().getTiles().size()) * GameConstants.TILE_SIZE;
        gameMapView.setPreferredSize(new Dimension(size, size));
        mapContainer.add(gameMapView, new GridBagConstraints());
        this.add(mapContainer, constraints);

        // Creates the scoreboard panel.
        scoreboardPanel();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        constraints.weighty = 0.2;
        JPanel panel = new JPanel(new BorderLayout());
        //panel.setBackground(Color.RED);
        panel.setBackground(Color.GREEN);
        panel.add(scoreboard, BorderLayout.NORTH);
        this.add(panel, constraints);

        // Creates the upper panel.
        this.menu = new MenuPanel(gameContext);
        menu.setBackground(Color.CYAN);
        //menu.setBackground(Color.BLACK);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.weighty = 0.05;
        this.add(menu, constraints);

        // Creates the bottom panel for the lives of the player.
        this.life = new LifePanel(gameContext);
        life.setBackground(Color.GREEN);
        JPanel lifeContainer = new JPanel(new GridBagLayout());
        GridBagConstraints lifeConstraint = new GridBagConstraints();
        lifeConstraint.fill = GridBagConstraints.BOTH;
        lifeConstraint.gridwidth = 2;
        lifeConstraint.weightx = 0.705;
        lifeConstraint.weighty = 0.2;
        lifeContainer.add(life, lifeConstraint);

        // Inner panel used to define the configuration.
        JPanel support = new JPanel();
        support.setBackground(Color.GREEN);
        lifeConstraint.fill = GridBagConstraints.BOTH;
        lifeConstraint.gridwidth = 1;
        lifeConstraint.weightx = 0.295;
        lifeConstraint.weighty = 0.2;
        lifeContainer.add(support, lifeConstraint);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.weighty = 0.08;
        this.add(lifeContainer, constraints);
    }

    private static class LifePanel extends JPanel {

        private GameContext gameContext;

        // TODO: Get Pacman ID
        LifePanel(GameContext gameContext) {
            this.gameContext = gameContext;
        }

        void setGameContext(GameContext gameContext) {
            this.gameContext = gameContext;
            this.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        }

        // TODO: Change when Pacman ID will be present.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.gameContext.getPacmans()
                    .stream()
                    .findFirst()
                    .ifPresent(x -> {
                        int radius = 26;
                        int space = 20;
                        int start = (this.getWidth() / 2) - (3 * radius) + space;
                        for (int i = 0; i < x.getLives(); i++) {
                            g.setColor(Color.RED);
                            g.fillOval(
                                    start + (radius + space) * i,
                                    -(radius / 2) + (this.getHeight() / 2),
                                    radius,
                                    radius
                            );
                        }
                    }
            );
        }
    }

    private static class MenuPanel extends JPanel {

        //private final JPanel timeContainer;
        private JLabel timeLeft;
        private GameContext gameContext;

        MenuPanel(GameContext gameContext) {
            this.gameContext = gameContext;
            this.setLayout(new BorderLayout());
            this.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, Color.BLACK));
            //this.timeContainer = new JPanel();
            timeLeft = new JLabel("Time left: " + gameContext.getGameState().getTimeLeft().getSeconds() + "s");
            timeLeft.setFont(FontManager.addingFont(18f, FONT_NAME));
            timeLeft.setForeground(Color.BLACK);
            timeLeft.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
            //timeContainer.add(timeLeft);
            this.add(timeLeft, BorderLayout.LINE_START);

            JPanel exitContainer = new JPanel(new GridBagLayout());
            exitContainer.setOpaque(false);
            JButton exit = new JButton("X");
            exit.setFont(FontManager.addingFont(18f, FONT_NAME));
            exit.setForeground(Color.BLACK);
            exit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            exit.setContentAreaFilled(false);
            exit.setBorderPainted(false);
            exit.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    exit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exit.setCursor(Cursor.getDefaultCursor());
                }
            });
            GridBagConstraints exitC = new GridBagConstraints();
            exitC.insets = new Insets(0, 10, 0, 10);
            exitContainer.add(exit, exitC);
            this.add(exitContainer, BorderLayout.LINE_END);
        }

        public void setGameContext(GameContext gameContext) {
            this.gameContext = gameContext;
            this.timeLeft.setText("Time left: " + gameContext.getGameState().getTimeLeft().getSeconds() + "s");
        }
    }

    private void scoreboardPanel() {
        this.scoreboard = new JPanel(new GridLayout(0,2));
        this.scoreboard.setOpaque(false);
        this.scoreboard.setBorder(BorderFactory.createEmptyBorder(50, 10, 0, 10));
        float titleScoreboardFontSize = 18f;
        JLabel playerName = new JLabel("Player name", SwingConstants.CENTER);
        playerName.setFont(FontManager.addingFont(titleScoreboardFontSize, FONT_NAME));
        this.scoreboard.add(playerName);
        JLabel scores = new JLabel("Scores", SwingConstants.CENTER);
        scores.setFont(FontManager.addingFont(titleScoreboardFontSize, FONT_NAME));
        this.scoreboard.add(scores);
        this.gameContext.getGameState().getLeaderboard().forEach(this::setScoreboardInfos);
        float scoreboardFontSize = 14f;
        Arrays.stream(this.scoreboard.getComponents())
                .filter(x -> x instanceof JLabel)
                .forEach(x -> {
                    x.setForeground(Color.BLACK);
                    ((JLabel) x).setBorder(new EmptyBorder(20, 0, 20, 0));
                    if (x.getFont().getSize2D() < titleScoreboardFontSize) {
                        x.setFont(FontManager.addingFont(scoreboardFontSize, FONT_NAME));
                    }
                    //((JLabel) x).setBorder(BorderFactory.createLineBorder(Color.BLACK));
                });
    }

    private void setScoreboardInfos(Pacman player, Integer score) {
        //TODO: Inserting here the setForeground
        String playerId = player.getId().toString();
        if (playerId.length() > PLAYER_NAME_LENGTH) {
            playerId = playerId.substring(0, PLAYER_NAME_LENGTH) + "...";
        }
        this.scoreboard.add(new JLabel(playerId, SwingConstants.CENTER));
        this.scoreboard.add(new JLabel(String.valueOf(score), SwingConstants.CENTER));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        scoreboardPanel();
        ((GameMapPanel) this.gameMapView).setGameContext(this.gameContext);
        ((MenuPanel) this.menu).setGameContext(this.gameContext);
        ((LifePanel) this.life).setGameContext(this.gameContext);
        this.mapContainer.repaint();
        this.menu.repaint();
        this.life.repaint();
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = Objects.requireNonNull(gameContext);
    }
}
