package it.unibo.controller.shared.network.sockets.codec;

import io.netty.buffer.ByteBuf;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;

import java.io.IOException;

/**
 * Contract for serializing and deserializing domain {@link NetworkPacket} instances
 * directly to and from Netty {@link ByteBuf} buffers.
 */
public interface NetworkPacketCodec {
    /**
     * Serializes a {@link NetworkPacket} into the provided Netty {@link ByteBuf}.
     *
     * @param packet the packet object to serialize
     * @param buffer the destination Netty buffer
     * @throws IOException if serialization fails
     */
    void encode(NetworkPacket packet, ByteBuf buffer) throws IOException;

    /**
     * Deserializes a {@link NetworkPacket} from the provided Netty {@link ByteBuf}.
     *
     * @param buffer the source Netty buffer containing packet bytes
     * @return the decoded {@link NetworkPacket} instance
     * @throws IOException if deserialization fails
     */
    NetworkPacket decode(ByteBuf buffer) throws IOException;
}