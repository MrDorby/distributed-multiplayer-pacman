package it.unibo.view.screens.game;

import it.unibo.view.screens.game.panels.ConnectionSetupPanel;

/**
 * Extends {@link AbstractGameScreen} by adding a custom {@link ConnectionSetupPanel}
 * card where users can manually enter host, port, and username details.
 * </p>
 */
public class GameScreenWithManualConnection extends AbstractGameScreen {
    public static final String CARD_SETUP = "Setup";
    private final ConnectionSetupPanel setupPanel = new ConnectionSetupPanel();

    public GameScreenWithManualConnection() {
        super();
        add(setupPanel, CARD_SETUP);
    }

    public ConnectionSetupPanel getSetupPanel() {
        return setupPanel;
    }

    public void showConnectionSetupView() {
        cardLayout.show(this, CARD_SETUP);
    }
}