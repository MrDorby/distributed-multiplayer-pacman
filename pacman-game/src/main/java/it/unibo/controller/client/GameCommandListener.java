package it.unibo.controller.client;

import it.unibo.controller.shared.input.PacmanCommand;

/**
 * Listener interface for intercepting player intents.
 */
public interface GameCommandListener {
    /**
     * Invoked whenever a local player action such as a movement request is registered.
     */
    void onGameCommand(PacmanCommand command);
}