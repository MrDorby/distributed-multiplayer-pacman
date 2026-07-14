package it.unibo.controller.shared.network.sockets.packets;

/**
 * Sent by the client over TCP when initially connecting to the server.
 *
 * <p> The server uses this packet to associate the player's identity with
 * their TCP connection.
 */
public record JoinGamePacket(String username) implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.JOIN_GAME;
    }
}
