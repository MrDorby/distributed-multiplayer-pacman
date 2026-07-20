package it.unibo.view.screens.game.panels;

import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;
import it.unibo.view.viewmodel.GameContextViewModel;
import it.unibo.view.viewmodel.PacmanViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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
        private GameContextViewModel context;

        void setGameContext(GameContextViewModel gameContext) {
            setDoubleBuffered(true);
            this.context = gameContext;
            this.setBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createEmptyBorder(20, 0, 20, 0),
                    "Lives",
                    TitledBorder.CENTER,
                    TitledBorder.ABOVE_TOP,
                    FontManager.addingFont(15f, FontName.S2P.getFontName()))
                );
        }

        // TODO: Change when Pacman ID will be present.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.context == null) return;
            this.context.pacmans()
                    .stream()
                    .findFirst()
                    .ifPresent(x -> {
                        int radius = 26;
                        int space = 20;
                        int start = (this.getWidth() / 2) - (3 * radius) + space;
                        for (int i = 0; i < x.lives(); i++) {
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

        public void setGameContext(GameContextViewModel context) {
            if (context == null || context.gameState() == null) {
                this.timeLeft.setText("Time left: --");
                return;
            }
            this.timeLeft.setText("Time left: " + context.gameState().timeLeftInMillis() / 1000 + "s");
        }
    }

    private static class ScoreboardPanel extends JPanel {
        private record Triple(JLabel name, JLabel lives, JLabel points) {
            
        }
        private final Map<Integer, Triple> scoreLabels = new HashMap<>();

        ScoreboardPanel() {
            this.setLayout(new GridLayout(0, 3));
            this.setOpaque(false);
            this.setBorder(BorderFactory.createEmptyBorder(50, 10, 0, 10));
            float titleScoreboardFontSize = 18f;
            JLabel playerName = new JLabel("Players", SwingConstants.CENTER);
            playerName.setFont(FontManager.addingFont(titleScoreboardFontSize, FONT_NAME));
            playerName.setForeground(Color.BLACK);
            playerName.setBorder(new EmptyBorder(20, 0, 20, 0));
            this.add(playerName);
            JLabel livesPLayers = new JLabel("Lives", SwingConstants.CENTER);
            livesPLayers.setFont(FontManager.addingFont(titleScoreboardFontSize, FONT_NAME));
            livesPLayers.setForeground(Color.BLACK);
            livesPLayers.setBorder(new EmptyBorder(20, 0, 20, 0));
            this.add(livesPLayers);
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
                JLabel lives = new JLabel("", SwingConstants.CENTER);
                lives.setFont(FontManager.addingFont(14f, FONT_NAME));
                lives.setForeground(Color.BLACK);
                lives.setBorder(new EmptyBorder(20, 0, 20, 0));
                JLabel points = new JLabel("", SwingConstants.CENTER);
                points.setFont(FontManager.addingFont(14f, FONT_NAME));
                points.setForeground(Color.BLACK);
                points.setBorder(new EmptyBorder(20, 0, 20, 0));
                scoreLabels.put(i, new Triple(id, lives, points));
                this.add(id);
                this.add(lives);
                this.add(points);
            }
        }

        public void setGameContext(GameContextViewModel context) {
            if (context == null || context.gameState() == null) return;
            var list = context.gameState().leaderboard()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .toList().reversed();
            for (int i = 0; i < list.size(); i++) {
                String id =  list.get(i).getKey();
                PacmanViewModel pc = context
                    .pacmans()
                    .stream()
                    .filter(
                        x -> x.id().equals(id))
                    .findFirst()
                    .get();
                String lives = String.valueOf(pc.lives());
                String points = String.valueOf(list.get(i).getValue());
                String baseDisplayName = pc.controlledByPlayer() ? id : " [Bot] " + id;
                String finalDisplayName = baseDisplayName.length() > PLAYER_NAME_LENGTH
                        ? baseDisplayName.substring(0, PLAYER_NAME_LENGTH) + "..."
                        : baseDisplayName;
                scoreLabels.get(i).name.setText(finalDisplayName);
                scoreLabels.get(i).lives.setText(lives);
                scoreLabels.get(i).points.setText(points);
            }
        }
    }

    public void setGameContext(GameContextViewModel context) {
        if (context == null ) return;
        this.gameMapPanel.setGameContext(context);
        this.menuPanel.setGameContext(context);
        this.lifePanel.setGameContext(context);
        this.scoreboardPanel.setGameContext(context);
        this.repaint();
    }
}
