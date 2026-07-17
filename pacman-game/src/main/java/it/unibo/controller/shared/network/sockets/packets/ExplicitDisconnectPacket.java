package it.unibo.controller.shared.network.sockets.packets;

public record ExplicitDisconnectPacket() implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.EXPLICIT_DISCONNECT;
    }
}
