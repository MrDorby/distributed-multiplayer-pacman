package it.unibo.controller.shared.engine;

import it.unibo.controller.shared.input.PacmanCommand;
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

    /**
     * Returns the current instance of the game.
     */
    Game getGame();

    /**
     * Starts the game loop.
     */
    void start();

    /**
     * Stops the game loop.
     */
    void stop();

    /**
     * Attaches view to the engine for rendering purposes.
     */
    void setView(GameView view);

    /**
    * Returns the rate in ticks per second at which this engine advances its simulation.
     */
    int getTickRate();
}
