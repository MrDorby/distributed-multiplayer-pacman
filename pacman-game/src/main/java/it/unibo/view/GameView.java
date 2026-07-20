package it.unibo.view;

import it.unibo.controller.shared.input.InputHandler;
import it.unibo.view.screens.game.panels.GamePanel;
import it.unibo.view.viewmodel.GameContextViewModel;

/**
 * The visual output for the game.
 */
public interface GameView {
    /**
     * Updates the screen with the latest game data.
     */
    void render(GameContextViewModel context);

    void setInputHandler(InputHandler inputHandler);

    /**
     * Return the JPanel used for rendering the game view
     */
    GamePanel getGamePanel();
}