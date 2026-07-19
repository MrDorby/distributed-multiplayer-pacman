package it.unibo.controller.shared.network.sockets.packets;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines all packet types exchanged between client and server.
 *
 * <p> Each packet type has a unique ID that is serialized as a single byte
 * in the network protocol to identify the payload that follows.
 */
public enum PacketType {
    JOIN_GAME(1, JoinServerPacket.class),
    JOIN_GAME_ACK(2, JoinGameAckPacket.class),
    UDP_HANDSHAKE(3, UdpHandshakePacket.class),
    GAME_START(4, GameStartPacket.class),
    PACMAN_MOVE_COMMAND(5, PacmanMovePacket.class),
    GAME_CONTEXT(6, GameContextPacket.class),
    GAME_ENDED(7, GameEndPacket.class),
    HEARTBEAT(8, HeartbeatPacket.class),
    EXPLICIT_DISCONNECT(9, ExplicitDisconnectPacket.class),
    UDP_HANDSHAKE_ACK(10, UdpHandshakeAckPacket.class);

    private static final Map<Byte, PacketType> PACKET_TYPE_BY_ID = new HashMap<>();

    static {
        for (PacketType packetType : PacketType.values()) {
            if (PACKET_TYPE_BY_ID.put(packetType.id, packetType) != null) {
                throw new IllegalArgumentException("Duplicate packet ID: " + packetType.id);
            }
        }
    }

    private final byte id;
    private final Class<? extends NetworkPacket> packetClass;

    PacketType(int id, Class<? extends NetworkPacket> packetClass) {
        this.id = (byte) id;
        this.packetClass = packetClass;
    }

    public byte getId() {
        return id;
    }

    public Class<? extends NetworkPacket> getPacketClass() {
        return packetClass;
    }

    public static PacketType fromId(byte id) {
        return PACKET_TYPE_BY_ID.get(id);
    }
}