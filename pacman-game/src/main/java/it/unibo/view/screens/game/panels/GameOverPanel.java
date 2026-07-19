package it.unibo.view.screens.game.panels;

import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.dto.GameStateDTO;
import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GameOverPanel extends JPanel {

    private Runnable onGoBackAction;

    private final JLabel winnerLabel;
    private final JPanel leaderboardPanel;

    public GameOverPanel() {
        setLayout(new GridLayout(3, 1));
        setBackground(Color.BLACK);

        JLabel title = new JLabel("Game Over");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setOpaque(false);
        title.setFont(FontManager.addingFont(50f, FontName.S2P.getFontName()));
        add(title);

        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setOpaque(false);

        winnerLabel = new JLabel("Winner: Loading...", SwingConstants.CENTER);
        winnerLabel.setForeground(Color.YELLOW);
        winnerLabel.setFont(FontManager.addingFont(24f, FontName.S2P.getFontName()));
        statsContainer.add(winnerLabel, BorderLayout.NORTH);

        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(leaderboardPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        statsContainer.add(scrollPane, BorderLayout.CENTER);

        add(statsContainer);

        JButton backButton = new JButton("Go Back");
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(Color.WHITE);
        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        backButton.setFont(FontManager.addingFont(40f, FontName.S2P.getFontName()));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton.addActionListener(_ -> {
            if (onGoBackAction != null) {
                onGoBackAction.run();
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        Border panelBorder = BorderFactory.createEmptyBorder(40, 140, 40, 140);
        buttonPanel.setBorder(panelBorder);
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
                        playerRow.setAlignmentX(Component.CENTER_ALIGNMENT);
                        playerRow.setFont(FontManager.addingFont(18f, FontName.S2P.getFontName()));
                        leaderboardPanel.add(playerRow);
                    });
        }
    }

    public void setOnGoBack(Runnable action) {
        this.onGoBackAction = action;
    }
}