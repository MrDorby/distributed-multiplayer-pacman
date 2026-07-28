package it.unibo.controller.shared.network.sockets.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Jackson-backed implementation of {@link NetworkPacketCodec}.
 * Handles binary format headers (Packet Type ID byte) and delegates payload
 * marshalling to an underlying Jackson {@link ObjectMapper}.
 */
public class JacksonPacketCodec implements NetworkPacketCodec {
    private final ObjectMapper mapper;

    public JacksonPacketCodec(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void encode(NetworkPacket packet, ByteBuf buffer) throws IOException {
        // Write the 1-byte protocol header identifying the packet type
        buffer.writeByte(packet.getType().getId());
        // Stream Jackson serialization directly into Netty's ByteBuf
        try (ByteBufOutputStream outputStream = new ByteBufOutputStream(buffer)) {
            mapper.writeValue((OutputStream) outputStream, packet);
        }
    }

    @Override
    public NetworkPacket decode(ByteBuf buffer) throws IOException {
        if (buffer.readableBytes() < 1) {
            throw new IOException("Buffer empty: Cannot read packet type ID");
        }
        // Read the 1-byte protocol header
        byte packetTypeId = buffer.readByte();
        PacketType type = PacketType.fromId(packetTypeId);
        if (type == null) {
            throw new IOException("Unknown packet type ID: " + packetTypeId);
        }
        // Stream Jackson deserialization directly from Netty's ByteBuf into the target class
        try (InputStream inputStream = new ByteBufInputStream(buffer)) {
            return mapper.readValue(inputStream, type.getPacketClass());
        }
    }
}