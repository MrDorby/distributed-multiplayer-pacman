package it.unibo.controller.shared.network.sockets.packets;

/**
 * Sent by both clients and servers over TCP to signal their reachability.
 */
public record HeartbeatPacket() implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.HEARTBEAT;
    }
}
