package it.unibo.view.screens.game;

import it.unibo.view.screens.game.panels.ConnectionSetupPanel;

/**
 * Extends {@link AbstractGameScreen} by adding a custom {@link ConnectionSetupPanel}
 * card where users can manually enter host, port, and username details.
 * </p>
 */
public class GameScreenWithManualConnection extends AbstractGameScreen {
    public static final String CARD_SETUP = "Setup";
    private final ConnectionSetupPanel connectionSetupPanel = new ConnectionSetupPanel();

    public GameScreenWithManualConnection() {
        super();
        add(connectionSetupPanel, CARD_SETUP);
    }

    public ConnectionSetupPanel getConnectionSetupPanel() {
        return connectionSetupPanel;
    }

    public void showConnectionSetupView() {
        cardLayout.show(this, CARD_SETUP);
    }
}