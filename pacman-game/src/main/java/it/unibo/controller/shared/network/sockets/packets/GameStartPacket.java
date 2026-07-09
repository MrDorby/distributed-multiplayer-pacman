package it.unibo.controller.shared.network.packets;

/**
 * Sent by the server over TCP to all connected clients to signal that the game has started.
 */
public record GameStartPacket() implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.GAME_START;
    }
}
