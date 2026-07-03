package it.unibo.controller.shared.network.packets;

public enum PacketType {
    JOIN_MATCH((byte) 1),
    UDP_HANDSHAKE((byte) 2),
    MOVE_COMMAND((byte) 3),
    GAME_START((byte) 4),
    GAME_CONTEXT((byte) 5),
    JOIN_ACK((byte) 6);

    private final byte id;

    PacketType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public static PacketType fromId(byte id) {
        for (PacketType type : values()) {
            if (type.id == id) return type;
        }
        throw new IllegalArgumentException("Unknown network packet ID: " + id);
    }
}