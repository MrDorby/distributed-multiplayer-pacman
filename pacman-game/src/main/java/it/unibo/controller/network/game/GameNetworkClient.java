package it.unibo.controller.network.game;

import it.unibo.controller.input.PacmanCommand;

public interface GameNetworkClient {
    void send(PacmanCommand command);

    void sendReliable(Object packet);
}
