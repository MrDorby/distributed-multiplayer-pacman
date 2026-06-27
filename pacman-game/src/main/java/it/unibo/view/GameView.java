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

    /**
     * @param onExit the action perfomed ones the user clicks on exit.
     * Displays the view for the winner of the match.
     */
    void displayWinView(Runnable onExit);

    /**
     * Displays the view of game over.
     */
    void displayGameOverView();
}