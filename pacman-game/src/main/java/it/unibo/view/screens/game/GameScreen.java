package it.unibo.view.screens.game;

import it.unibo.view.GameView;
import it.unibo.view.SwingGameView;
import it.unibo.view.screens.game.panels.ConnectingPanel;
import it.unibo.view.screens.game.panels.ConnectionFailurePanel;
import it.unibo.view.screens.game.panels.ConnectionSetupPanel;
import it.unibo.view.screens.game.panels.GameOverPanel;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {
    public static final String CARD_SETUP = "Setup";
    public static final String CARD_CONNECTING = "Connecting";
    public static final String CARD_GAME = "Game";
    public static final String CARD_GAME_OVER = "GameOver";
    public static final String CARD_FAILURE = "ConnectionFailure";

    private final CardLayout cardLayout = new CardLayout();
    private final GameView gameView = new SwingGameView();

    private final ConnectionSetupPanel connectionSetupPanel = new ConnectionSetupPanel();
    private final ConnectingPanel connectingPanel = new ConnectingPanel();
    private final GameOverPanel gameOverPanel = new GameOverPanel();
    private final ConnectionFailurePanel failurePanel = new ConnectionFailurePanel();

    public GameScreen() {
        setLayout(cardLayout);
        add(connectionSetupPanel, CARD_SETUP);
        add(connectingPanel, CARD_CONNECTING);
        add(this.gameView.getGamePanel(), CARD_GAME);
        add(gameOverPanel, CARD_GAME_OVER);
        add(failurePanel, CARD_FAILURE);
    }

    public ConnectionSetupPanel getSetupPanel() {
        return connectionSetupPanel;
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

    public void showConnectionSetupView() {
        cardLayout.show(this, CARD_SETUP);
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