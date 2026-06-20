package it.unibo.controller.network.game;

import it.unibo.controller.commands.PacmanCommand;

public interface GameNetworkClient {
    void send(PacmanCommand command);

    void sendReliable(Object packet);
}
