package it.unibo.controller.shared.network.packets;

import it.unibo.model.common.Direction;

/**
 * Sent by the client over UDP to request a directional movement change for a specific Pacman.
 */
public record PacmanMovePacket(String pacmanId, Direction direction) implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.PACMAN_MOVE_COMMAND;
    }
}
