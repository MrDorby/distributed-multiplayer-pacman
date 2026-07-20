package it.unibo.controller.shared.engine;

import it.unibo.controller.shared.engine.command.PacmanCommand;
import it.unibo.view.GameView;

public interface GameEngine {

    /**
     * Adds a command to be executed on the next tick.
     */
    void enqueueCommand(PacmanCommand command);

    /**
     * Ticks processed in the last second.
     */
    int getCurrentTps();

    /**
     * Returns whether the game engine is still running.
     */
    boolean isRunning();

    /**
     * Starts the game engine.
     */
    void start();

    /**
     * Stops the game engine.
     */
    void stop();

    /**
     * Attaches the view to the engine for rendering purposes.
     */
    void setView(GameView view);

    /**
    * Returns the rate in ticks per second at which this engine advances its simulation.
     */
    int getTickRate();
}
