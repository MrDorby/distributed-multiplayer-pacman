package it.unibo.view.screens.game;

import it.unibo.view.GameView;
import it.unibo.view.SwingGameView;
import it.unibo.view.screens.game.panels.ConnectingPanel;
import it.unibo.view.screens.game.panels.ConnectionFailurePanel;
import it.unibo.view.screens.game.panels.GameOverPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract container managing common game view cards via {@link CardLayout}.
 * <p>
 * Standardizes UI view switching across connection states: connecting, active gameplay,
 * game over summaries, and connection failure screens.
 */
public abstract class AbstractGameScreen extends JPanel {
    public static final String CARD_CONNECTING = "Connecting";
    public static final String CARD_GAME = "Game";
    public static final String CARD_GAME_OVER = "GameOver";
    public static final String CARD_FAILURE = "ConnectionFailure";

    protected final CardLayout cardLayout = new CardLayout();

    protected final ConnectingPanel connectingPanel = new ConnectingPanel();
    protected final GameView gameView = new SwingGameView();
    protected final GameOverPanel gameOverPanel = new GameOverPanel();
    protected final ConnectionFailurePanel failurePanel = new ConnectionFailurePanel();

    public AbstractGameScreen() {
        setLayout(cardLayout);
        add(connectingPanel, CARD_CONNECTING);
        add(gameView.getGamePanel(), CARD_GAME);
        add(gameOverPanel, CARD_GAME_OVER);
        add(failurePanel, CARD_FAILURE);
    }

    public ConnectingPanel getConnectingPanel() {
        return connectingPanel;
    }

    public GameOverPanel getGameOverPanel() {
        return gameOverPanel;
    }

    public ConnectionFailurePanel getFailurePanel() {
        return failurePanel;
    }

    public GameView getGameView() {
        return gameView;
    }

    public void showConnectingView(String statusText) {
        connectingPanel.updateStatus(statusText);
        cardLayout.show(this, CARD_CONNECTING);
    }

    public void showGameView() {
        cardLayout.show(this, CARD_GAME);
        gameView.getGamePanel().requestFocusInWindow();
    }

    public void showGameOverView() {
        cardLayout.show(this, CARD_GAME_OVER);
    }

    public void showFailureView(String reasonText) {
        failurePanel.setErrorMessage(reasonText);
        cardLayout.show(this, CARD_FAILURE);
    }
}