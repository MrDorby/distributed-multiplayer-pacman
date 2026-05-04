package it.unibo.view;

import it.unibo.model.common.GameConstants;
import it.unibo.model.entities.Pacman;
import it.unibo.model.game.GameContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.Duration;import java.util.Arrays;
import java.util.Objects;

public class GamePanel extends JPanel {

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
        mapContainer.setBackground(Color.YELLOW);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 0.8;
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
        constraints.weightx = 0.2;
        constraints.weighty = 0.2;
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.RED);
        panel.add(scoreboard, BorderLayout.NORTH);
        this.add(panel, constraints);

        // Creates the upper panel.
        this.menu = new MenuPanel(gameContext);
        menu.setBackground(Color.CYAN);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.weighty = 0.05;
        this.add(menu, constraints);

        // Creates the bottom panel for the lives of the player.
        this.life = new JPanel();
        life.setBackground(Color.GREEN);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.weighty = 0.08;
        this.add(life, constraints);
    }

    private static class MenuPanel extends JPanel {

        //private final JPanel timeContainer;
        private JLabel timeLeft;
        private GameContext gameContext;

        public MenuPanel(GameContext gameContext) {
            this.setLayout(new BorderLayout());
            //this.timeContainer = new JPanel();
            timeLeft = new JLabel("Time left: " + gameContext.getGameState().getTimeLeft().getSeconds() + "s");
            timeLeft.setForeground(Color.BLACK);
            //timeContainer.add(timeLeft);
            this.add(timeLeft, BorderLayout.LINE_START);
        }

        public void setGameContext(GameContext gameContext) {
            this.gameContext = gameContext;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            timeLeft = new JLabel(gameContext.getGameState().getTimeLeft().getSeconds() + "s");
        }}

    private void scoreboardPanel() {
        this.scoreboard = new JPanel(new GridLayout(0,2));
        //((GridLayout) this.scoreboard.getLayout()).setVgap(20);
        //this.scoreboard.setBackground(Color.RED);
        //this.scoreboard.setBorder(new LineBorder(Color.BLACK, 2));
        this.scoreboard.setOpaque(false);
        this.scoreboard.add(new JLabel("Player name", SwingConstants.CENTER));
        this.scoreboard.add(new JLabel("Scores", SwingConstants.CENTER));
        this.gameContext.getGameState().getLeaderboard().forEach(this::setScoreboardInfos);
        Arrays.stream(this.scoreboard.getComponents())
                .filter(x -> x instanceof JLabel)
                .forEach(x -> {
                    x.setForeground(Color.WHITE);
                    ((JLabel) x).setBorder(new EmptyBorder(20, 0, 20, 0));
                });
    }

    private void setScoreboardInfos(Pacman player, Integer score) {
        //TODO: Inserting here the setForeground
        this.scoreboard.add(new JLabel(player.getId().toString()));
        this.scoreboard.add(new JLabel(String.valueOf(score)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        scoreboardPanel();
        ((GameMapPanel) this.gameMapView).setGameContext(this.gameContext);
        ((MenuPanel) this.menu).setGameContext(this.gameContext);
        this.mapContainer.repaint();
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = Objects.requireNonNull(gameContext);
    }
}
