package it.unibo.controller.shared.network.packets;

/**
 * Represents a generic data packet transmitted over the network.
 * All specific packet types must implement this interface to define their payload type.
 */
public interface NetworkPacket {
    /**
     * Gets the unique identifier type associated with this packet implementation.
     * <p> This metadata token is serialized alongside the payload, allowing the receiving
     * end of the pipeline to identify and deserialize the binary data back into an object.
     *
     * @return the {@link PacketType} for this packet instance
     */
    PacketType getType();
}
