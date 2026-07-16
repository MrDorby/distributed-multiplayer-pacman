package it.unibo.controller.shared.network.sockets.packets;

public record UdpHandshakeAckPacket() implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.UDP_HANDSHAKE_ACK;
    }
}