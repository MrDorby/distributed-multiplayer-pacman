package it.unibo.controller.shared.network.packets;

/**
 * Sent by the server over TCP after accepting a client connection.
 *
 * <p> Confirms that the server has registered the client's TCP session and
 * is ready to receive the UDP handshake.
 */
public record JoinGameAckPacket() implements NetworkPacket{
    @Override
    public PacketType getType() {
        return PacketType.JOIN_GAME_ACK;
    }
}