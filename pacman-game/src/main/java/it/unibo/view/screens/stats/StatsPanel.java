package it.unibo.view.screens.stats;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import it.unibo.controller.client.common.Stats;
import it.unibo.view.font.FontManager;
import it.unibo.view.font.FontName;

public class StatsPanel extends JPanel {
    
    private static final String FONT_NAME = FontName.S2P.getFontName();

    private static final String N_MATCH = "N. Matches";
    private static final String N_WINS = "N. Wins";
    private static final String WIN_RATE = "Win Rate";
    private static final String BEST_SCORE = "Best Score";

    private static final float SIZE_LABEL_TITLE = 22f;
    private static final float SIZE_TITLE = 24f;
    private static final float SIZE_LABEL_TEXT = 20f;
    private static final float SIZE_BUTTON = 22f;

    private static final Border TITLES_BORDER = BorderFactory.createMatteBorder(0,0, 2, 0, Color.BLACK);
    private static final Border TEXT_BORDER = BorderFactory.createEmptyBorder(30, 0, 0, 0);

    private final JLabel title = new JLabel();
    private final JLabel matchesTitle = new JLabel(N_MATCH);
    private final JLabel winsTitle = new JLabel(N_WINS);
    private final JLabel winRateTitle = new JLabel(WIN_RATE);
    private final JLabel bestScoreTitle = new JLabel(BEST_SCORE);

    private final JLabel matches = new JLabel();
    private final JLabel wins = new JLabel();
    private final JLabel winRate = new JLabel();
    private final JLabel bestScore = new JLabel();

    private final JPanel titlePanel = new JPanel(new FlowLayout());
    private final JPanel statsPanel = new JPanel(new GridLayout(2, 2));
    private final JPanel buttonPanel = new JPanel();

    private final JButton homeButton = new JButton("Home");

    public StatsPanel(String username) {
        setLayout(new BorderLayout());
        setBackground(Color.CYAN);

        if (Objects.isNull(username)) {
            title.setText("Player");
        } else {
            title.setText(username);
        }
        title.setFont(FontManager.addingFont(SIZE_TITLE, FONT_NAME));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(
            BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK), 
            BorderFactory.createEmptyBorder(30, 0, 28, 0))
        );
        titlePanel.add(title);

        statsPanel.setOpaque(false);
        JPanel matchContainer = setContainer(matchesTitle, matches);
        statsPanel.add(matchContainer);

        JPanel winsContainer = setContainer(winsTitle, wins);
        statsPanel.add(winsContainer);

        JPanel winRateContainer = setContainer(winRateTitle, winRate);
        statsPanel.add(winRateContainer);

        JPanel bestScoreContainer = setContainer(bestScoreTitle, bestScore);
        statsPanel.add(bestScoreContainer);

        JPanel buttonContainer = new JPanel(new FlowLayout());
        buttonContainer.setOpaque(false);
        homeButton.setFont(FontManager.addingFont(SIZE_BUTTON, FONT_NAME));
        homeButton.setBackground(Color.ORANGE);
        homeButton.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(20, 40, 20, 40))
            );
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonContainer.add(homeButton);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(buttonContainer);
        buttonPanel.setBorder(
            BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK), 
            BorderFactory.createEmptyBorder(18, 0, 20, 0))
        );

        this.add(titlePanel, BorderLayout.PAGE_START);
        this.add(statsPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.PAGE_END);

    }

    private JPanel setContainer(JLabel title, JLabel value) {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JPanel titleContainer = new JPanel(new FlowLayout());
        titleContainer.setOpaque(false);
        title.setBorder(TITLES_BORDER);
        title.setFont(FontManager.addingFont(SIZE_LABEL_TITLE, FONT_NAME));
        titleContainer.add(title);
        JPanel valueContainer = new JPanel(new FlowLayout());
        valueContainer.setOpaque(false);
        value.setBorder(TEXT_BORDER);
        value.setFont(FontManager.addingFont(SIZE_LABEL_TEXT, FONT_NAME));
        valueContainer.add(value);
        container.add(titleContainer);
        container.add(valueContainer);
        JPanel finalContainer = new JPanel(new GridBagLayout());
        finalContainer.setOpaque(false);
        finalContainer.add(container);
        return finalContainer;
    }

    /**
     * Adds the player statistics to the differents labels.
     * @param stats the user's info.
     */
    public void setStats(Stats stats) {
        if (Objects.nonNull(stats)) {
            matches.setText(String.valueOf(stats.nMatch()));
            wins.setText(String.valueOf(stats.nWins()));
            winRate.setText(String.valueOf(stats.winRate()));
            bestScore.setText(String.valueOf(stats.bestScore()));
        } else {
            matches.setText(" - ");
            wins.setText(" - ");
            winRate.setText(" - ");
            bestScore.setText(" - ");
        }
    }

    /**
     * Shows a Message Dialog to inform user. 
     * @param message the content of the dialog.
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Adds an action listener to the home button.
     * @param action the action performed by the button.
     */
    public void onHomeButton(Runnable action) {
        homeButton.addActionListener(e -> action.run());
    }

}
