package it.unibo.controller.server;

import it.unibo.controller.shared.input.PacmanMoveCommand;

/**
 * Receives events from the network layer as clients join and send commands.
 */
public interface GameServerNetworkListener {
    /**
     * Called when a player joins the server.
     *
     * @param username the username of the player who joined
     */
    void onPlayerJoined(String username);

    /**
     * Called when a move command is received from a client.
     *
     * @param username the username of the player who sent the command
     * @param command  the move command received
     */
    void onCommandReceived(String username, PacmanMoveCommand command);
}