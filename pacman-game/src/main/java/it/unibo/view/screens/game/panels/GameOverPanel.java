package it.unibo.view.screens.game.panels;

import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.dto.GameStateDTO;
import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {

    private static final String FONT_NAME = FontName.S2P.getFontName();
    private static final float TITLE_SIZE = 50f;
    private static final float WIN_LABEL_SIZE = 26f;
    private static final float LEADERBOARD_SIZE = 18f;
    private static final float BUTTON_SIZE = 30f;

    private final JPanel titlePanel = new JPanel();
    private final JPanel winnerPanel = new JPanel();
    private final JPanel leaderboardPanel = new JPanel();
    private final JPanel buttonPanel = new JPanel();

    private final JLabel titleLabel = new JLabel("Game Over");
    private final JLabel winnerLabel = new JLabel("Winner: Loading...", SwingConstants.CENTER);

    private final JButton backButton = new JButton("Go Back");

    public GameOverPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);

        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(FontManager.addingFont(TITLE_SIZE, FONT_NAME));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        titlePanel.add(titleLabel);
        add(titlePanel);

        JPanel statsContainer = new JPanel();
        statsContainer.setOpaque(false);
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));

        winnerLabel.setForeground(Color.YELLOW);
        winnerLabel.setFont(FontManager.addingFont(WIN_LABEL_SIZE, FONT_NAME));
        winnerPanel.setOpaque(false);
        winnerPanel.add(winnerLabel);
        winnerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        statsContainer.add(winnerPanel);

        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setOpaque(false);
        statsContainer.add(leaderboardPanel);

        add(statsContainer);

        backButton.setForeground(Color.BLACK);
        backButton.setBackground(Color.WHITE);
        backButton.setFont(FontManager.addingFont(BUTTON_SIZE, FONT_NAME));
        backButton.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        add(buttonPanel);
    }

    public void updateStats(GameContextDTO context) {
        if (context == null || context.gameState() == null) return;
        GameStateDTO state = context.gameState();
        if (state.winnerId() != null) {
            winnerLabel.setText("Winner: " + state.winnerId());
        } else {
            winnerLabel.setText("It's a draw!");
        }
        leaderboardPanel.removeAll();
        if (state.leaderboard() != null) {
            state.leaderboard().entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .forEach(entry -> {
                        JLabel playerRow = new JLabel(entry.getKey() + " : " + entry.getValue() + " pts");
                        playerRow.setForeground(Color.WHITE);
                        playerRow.setFont(FontManager.addingFont(LEADERBOARD_SIZE, FONT_NAME));
                        JPanel panelRow = new JPanel();
                        panelRow.setOpaque(false);
                        //panelRow.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                        panelRow.add(playerRow);
                        leaderboardPanel.add(panelRow);
                    });
        }
    }

    public void setOnGoBack(Runnable action) {
        backButton.addActionListener(e -> action.run());
    }
}