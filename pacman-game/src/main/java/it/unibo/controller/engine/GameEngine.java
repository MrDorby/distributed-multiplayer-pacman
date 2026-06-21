package it.unibo.controller.engine;

import it.unibo.controller.input.PacmanCommand;
import it.unibo.model.game.Game;
import it.unibo.view.GameView;

public interface GameEngine {
    /**
     * Adds a command to be executed on the next tick.
     */
    void enqueueCommand(PacmanCommand command);

    /**
     * Ticks processed in the last completed second.
     */
    int getCurrentTps();

    /**
     * Returns whether the loop is still running.
     */
    boolean isRunning();

    Game getGame();

    /**
     * Starts the game loop.
     */
    void start();

    /**
     * Stops the game loop.
     */
    void stop();

    void setView(GameView view);
}
