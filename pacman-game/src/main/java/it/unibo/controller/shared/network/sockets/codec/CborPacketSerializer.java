package it.unibo.controller.shared.network.sockets.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CborPacketSerializer {
    private static final ObjectMapper MAPPER = new CBORMapper();

    private CborPacketSerializer() {}

    public static void serialize(NetworkPacket packet, ByteBuf buffer) throws IOException {
        buffer.writeByte(packet.getType().getId());
        try (ByteBufOutputStream outputStream = new ByteBufOutputStream(buffer)) {
            MAPPER.writeValue((OutputStream) outputStream, packet);
        }
    }

    public static NetworkPacket deserialize(ByteBuf buffer) throws IOException {
        if (buffer.readableBytes() < 1) {
            throw new IOException("Buffer empty: Cannot read packet type ID");
        }
        byte packetType = buffer.readByte();
        PacketType type = PacketType.fromId(packetType);
        if (type == null) {
            throw new IOException("Unknown packet type ID: " + packetType);
        }
        Class<? extends NetworkPacket> targetClass = type.getPacketClass();
        try (var inputStream = new ByteBufInputStream(buffer)) {
            return MAPPER.readValue((InputStream) inputStream, targetClass);
        }
    }
}
