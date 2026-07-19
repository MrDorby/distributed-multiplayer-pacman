package it.unibo.controller.shared.network.sockets.packets;

import it.unibo.controller.shared.network.dto.GameContextDTO;

/**
 * Sent by the server over TCP to signal that the game session has ended.
 */
public record GameEndPacket(GameContextDTO context) implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.GAME_ENDED;
    }
}
