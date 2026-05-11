package it.unibo.view;

import it.unibo.model.game.GameContext;

/**
 * The visual output for the game.
 */
public interface GameView {

    /**
     * Updates the screen with the latest game data.
     */
    void render(GameContext context);

    /**
     * Displays the game window.
     */
    void show();
}