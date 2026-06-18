package it.unibo.controller.network;

import it.unibo.controller.commands.PacmanCommand;

public interface NetworkClient {
    void send(PacmanCommand command);

    void sendReliable(Object packet);
}
