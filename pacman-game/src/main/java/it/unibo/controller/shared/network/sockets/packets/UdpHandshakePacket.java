package it.unibo.controller.shared.network.sockets.packets;

/**
 * Sent by the client over UDP after the TCP connection is established.
 *
 * <p> The server uses the source address of this packet to discover and
 * register the client's UDP endpoint.
 */
public record UdpHandshakePacket(String token) implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.UDP_HANDSHAKE;
    }
}