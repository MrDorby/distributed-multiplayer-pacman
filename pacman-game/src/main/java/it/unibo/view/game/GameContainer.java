package it.unibo.view.game;

import it.unibo.view.GameView;
import it.unibo.view.SwingGameView;

import javax.swing.*;
import java.awt.*;

public class GameContainer extends JPanel {
    public interface ViewListener {
        void onConnectRequested(String username, String host, int tcp, int udp);
        void onCancelRequested();
    }

    private static final String CARD_SETUP = "Setup";
    private static final String CARD_CONNECTING = "Connecting";
    private static final String CARD_GAME = "Game";

    private final CardLayout cardLayout = new CardLayout();
    private final GameView gameView = new SwingGameView();
    private final ConnectionStatusPanel connectionStatusPanel;

    private ConnectionSetupPanel setupPanel;
    private ViewListener listener;

    public GameContainer() {
        setLayout(cardLayout);
        this.connectionStatusPanel = new ConnectionStatusPanel(_ -> {
            if (listener != null) listener.onCancelRequested();
        });
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
        this.setupPanel = new ConnectionSetupPanel(listener);
        add(setupPanel, CARD_SETUP);
        add(connectionStatusPanel, CARD_CONNECTING);
        add(this.gameView.getGamePanel(), CARD_GAME);
    }

    public GameView getGameView() {
        return gameView;
    }

    public void showConnectionSetupView() {
        cardLayout.show(this, CARD_SETUP);
    }

    public void showConnectingStatusView(String initialStatus) {
        connectionStatusPanel.updateStatus(initialStatus, false);
        cardLayout.show(this, CARD_CONNECTING);
    }

    public void showGameView() {
        cardLayout.show(this, CARD_GAME);
        gameView.getGamePanel().requestFocusInWindow();
    }

    public void updateStatusPanel(String text, boolean isError) {
        connectionStatusPanel.updateStatus(text, isError);
    }
}