package it.unibo.controller.server;

import it.unibo.controller.shared.engine.command.PacmanMoveCommand;

/**
 * Receives events from the network layer as clients join and send commands.
 */
public interface GameServerNetworkListener {
    /**
     * Called when a move command is received from a client.
     *
     * @param command  the move command received
     */
    void onCommandReceived(PacmanMoveCommand command);
}