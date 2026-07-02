package it.unibo.controller.server.network;

import it.unibo.controller.shared.input.PacmanMoveCommand;

public interface GameServerNetworkListener {
    void onPlayerJoined(String username);

    void onCommandReceived(String username, PacmanMoveCommand command);
}