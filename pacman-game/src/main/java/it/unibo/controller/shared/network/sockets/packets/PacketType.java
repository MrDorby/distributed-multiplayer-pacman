package it.unibo.controller.shared.network.packets;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines all packet types exchanged between client and server.
 *
 * <p> Each packet type has a unique ID that is serialized as a single byte
 * in the network protocol to identify the payload that follows.
 */
public enum PacketType {
    JOIN_GAME(1),
    JOIN_GAME_ACK(2),
    UDP_HANDSHAKE(3),
    GAME_START(4),
    PACMAN_MOVE_COMMAND(5),
    GAME_CONTEXT(6);

    private static final Map<Byte, PacketType> PACKET_TYPE_BY_ID = new HashMap<>();

    static {
        for (PacketType packetType : PacketType.values()) {
            if (PACKET_TYPE_BY_ID.put(packetType.id, packetType) != null) {
                throw new IllegalArgumentException("Duplicate packet ID: " + packetType.id);
            }
        }
    }

    private final byte id;

    PacketType(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }

    public static PacketType fromId(byte id) {
        PacketType type = PACKET_TYPE_BY_ID.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown packet ID: " + Byte.toUnsignedInt(id));
        }
        return type;
    }
}