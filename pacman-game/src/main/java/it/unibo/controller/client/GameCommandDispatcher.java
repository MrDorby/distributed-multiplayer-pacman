package it.unibo.controller.client.network;

import it.unibo.controller.shared.input.PacmanCommand;

public interface GameCommandDispatcher {
    void sendMoveCommand(PacmanCommand command);
}