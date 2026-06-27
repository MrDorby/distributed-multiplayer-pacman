package it.unibo.view.screens.game;

import it.unibo.model.game.GameContext;
import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ch.qos.logback.core.joran.sanity.Pair;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

public class GamePanel extends JPanel {

    private final static int PLAYER_NAME_LENGTH = 12;
    private final static String FONT_NAME = FontName.S2P.getFontName();
    private final ScoreboardPanel scoreboardPanel;
    private final GameMapPanel gameMapPanel;
    private final MenuPanel menuPanel;
    private final LifePanel lifePanel;

    private Runnable onEscapePressed;

    public GamePanel() {
        this.setBackground(Color.BLACK);
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // Creates the panel for the map of the game.
        JPanel mapPanel = new JPanel(new BorderLayout());
        mapPanel.setBackground(Color.YELLOW);

        this.gameMapPanel = new GameMapPanel();
        mapPanel.add(gameMapPanel, BorderLayout.CENTER);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 0.9;
        constraints.weighty = 1;
        
        this.lifePanel = new LifePanel();
        this.lifePanel.setOpaque(false);
        mapPanel.add(lifePanel, BorderLayout.SOUTH);
        this.add(mapPanel, constraints);

        // Creates the scoreboard panel.
        this.scoreboardPanel = new ScoreboardPanel();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        constraints.weighty = 0.2;
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.YELLOW);
        panel.add(scoreboardPanel, BorderLayout.NORTH);
        this.add(panel, constraints);

        // Creates the upper panel.
        this.menuPanel = new MenuPanel(() -> {
            if (onEscapePressed != null) onEscapePressed.run();
        });

        menuPanel.setBackground(Color.CYAN);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.weighty = 0;
        this.add(menuPanel, constraints);
    }

    public void onEscape(Runnable action) {
        this.onEscapePressed = action;
    }

    private static class LifePanel extends JPanel {
        private GameContext gameContext;

        void setGameContext(GameContext gameContext) {
            this.gameContext = gameContext;
            this.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        }

        // TODO: Change when Pacman ID will be present.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.gameContext == null) return;
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
        private final JLabel timeLeft;

        MenuPanel(Runnable onExitAction) {
            this.setLayout(new BorderLayout());
            this.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, Color.BLACK));
            timeLeft = new JLabel("Time left: --");
            timeLeft.setFont(FontManager.addingFont(18f, FONT_NAME));
            timeLeft.setForeground(Color.BLACK);
            timeLeft.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
            this.add(timeLeft, BorderLayout.LINE_START);

            JPanel exitContainer = new JPanel(new GridBagLayout());
            exitContainer.setOpaque(false);
            JButton exit = new JButton("X");
            exit.setFont(FontManager.addingFont(18f, FONT_NAME));
            exit.setForeground(Color.BLACK);
            exit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            exit.setContentAreaFilled(false);
            exit.setBorderPainted(false);
            exit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            exit.addActionListener(e -> onExitAction.run());
            GridBagConstraints exitC = new GridBagConstraints();
            exitC.insets = new Insets(0, 10, 0, 10);
            exitContainer.add(exit, exitC);
            this.add(exitContainer, BorderLayout.LINE_END);
        }

        public void setGameContext(GameContext gameContext) {
            if (gameContext == null || gameContext.getGameState() == null) {
                this.timeLeft.setText("Time left: --");
                return;
            }
            this.timeLeft.setText("Time left: " + gameContext.getGameState().getTimeLeftInMillis() / 1000 + "s");
        }
    }

    private static class ScoreboardPanel extends JPanel {
        private record Pair(JLabel name, JLabel points) {
            
        }
        //private final Map<String, JLabel> scoreLabels = new HashMap<>();
        private final Map<Integer, Pair> scoreLabels = new HashMap<>();

        ScoreboardPanel() {
            this.setLayout(new GridLayout(0, 2));
            this.setOpaque(false);
            this.setBorder(BorderFactory.createEmptyBorder(50, 10, 0, 10));
            float titleScoreboardFontSize = 18f;
            JLabel playerName = new JLabel("Player name", SwingConstants.CENTER);
            playerName.setFont(FontManager.addingFont(titleScoreboardFontSize, FONT_NAME));
            playerName.setForeground(Color.BLACK);
            playerName.setBorder(new EmptyBorder(20, 0, 20, 0));
            this.add(playerName);
            JLabel scores = new JLabel("Scores", SwingConstants.CENTER);
            scores.setFont(FontManager.addingFont(titleScoreboardFontSize, FONT_NAME));
            scores.setForeground(Color.BLACK);
            scores.setBorder(new EmptyBorder(20, 0, 20, 0));
            this.add(scores);
            for (int i = 0; i < 4; i++) {
                JLabel id = new JLabel("", SwingConstants.CENTER);
                id.setFont(FontManager.addingFont(14f, FONT_NAME));
                id.setForeground(Color.BLACK);
                id.setBorder(new EmptyBorder(20, 0, 20, 0));
                JLabel points = new JLabel("", SwingConstants.CENTER);
                points.setFont(FontManager.addingFont(14f, FONT_NAME));
                points.setForeground(Color.BLACK);
                points.setBorder(new EmptyBorder(20, 0, 20, 0));
                scoreLabels.put(i, new Pair(id, points));
                this.add(id);
                this.add(points);
            }
        }

        // TODO: completing with username deleted for players with no more lives.
        public void setGameContext(GameContext gameContext) {
            if (gameContext == null || gameContext.getGameState() == null) return;
            var list = gameContext.getGameState().getLeaderboard()
                .entrySet()
                .stream()
                .sorted((x, y) -> x.getValue().compareTo(y.getValue()))
                .collect(Collectors.toList()).reversed();
            for (int i = 0; i < list.size(); i++) {
                String playerId = list.get(i).getKey();
                String points = String.valueOf(list.get(i).getValue());
                String displayName = playerId.length() > PLAYER_NAME_LENGTH
                        ? playerId.substring(0, PLAYER_NAME_LENGTH) + "..."
                        : playerId;
                scoreLabels.get(i).name.setText(displayName);
                scoreLabels.get(i).points.setText(points);
            }
            /*{
                    String playerId = x.getKey();
                    JLabel scoreLabel = scoreLabels.get(playerId);
                    if (scoreLabel == null) {
                        String displayName = playerId.length() > PLAYER_NAME_LENGTH
                                ? playerId.substring(0, PLAYER_NAME_LENGTH) + "..."
                                : playerId;
                        JLabel nameLabel = new JLabel(displayName, SwingConstants.CENTER);
                        nameLabel.setFont(FontManager.addingFont(14f, FONT_NAME));
                        nameLabel.setForeground(Color.BLACK);
                        nameLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
                        scoreLabel = new JLabel(String.valueOf(x.getValue()), SwingConstants.CENTER);
                        scoreLabel.setFont(FontManager.addingFont(14f, FONT_NAME));
                        scoreLabel.setForeground(Color.BLACK);
                        scoreLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
                        this.add(nameLabel);
                        this.add(scoreLabel);
                        scoreLabels.put(playerId, scoreLabel);
                    } else {
                        scoreLabel.setText(String.valueOf(x.getValue()));
                    }
                } */
        }
    }

    public void setGameContext(GameContext gameContext) {
        Objects.requireNonNull(gameContext);
        this.gameMapPanel.setGameContext(gameContext);
        this.menuPanel.setGameContext(gameContext);
        this.lifePanel.setGameContext(gameContext);
        this.scoreboardPanel.setGameContext(gameContext);
        this.repaint();
    }
}
