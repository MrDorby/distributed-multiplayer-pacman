package it.unibo.controller.shared.network.sockets.packets;

/**
 * Sent by the server over TCP to signal that the game session has ended.
 */
public class GameEndedPacket implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.GAME_ENDED;
    }
}
