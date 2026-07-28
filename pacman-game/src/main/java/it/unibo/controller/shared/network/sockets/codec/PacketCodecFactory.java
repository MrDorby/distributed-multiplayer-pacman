package it.unibo.controller.shared.network.sockets.codec;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import it.unibo.controller.shared.network.dto.*;

/**
 * Factory responsible for providing thread-safe {@link NetworkPacketCodec} instances.
 */
public class PacketCodecFactory {

    /**
     * Marker mixin applied to DTO records.
     * Forces Jackson to serialize records as ordered CBOR arrays instead of key-value objects.
     * This strips all field name strings from payload, reducing bandwidth for high-frequency engine broadcasts.
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private interface ArrayMixin {}

    private static final NetworkPacketCodec COMPACT_CBOR_CODEC = new JacksonPacketCodec(
            CBORMapper.builder()
                    .addMixIn(GameContextDTO.class, ArrayMixin.class)
                    .addMixIn(PacmanDTO.class, ArrayMixin.class)
                    .addMixIn(GhostDTO.class, ArrayMixin.class)
                    .addMixIn(GameStateDTO.class, ArrayMixin.class)
                    .addMixIn(DotDTO.class, ArrayMixin.class)
                    .build()
    );

    private static final NetworkPacketCodec KEY_VALUE_CBOR_CODEC =
            new JacksonPacketCodec(new CBORMapper());

    private static final NetworkPacketCodec KEY_VALUE_JSON_CODEC =
            new JacksonPacketCodec(new ObjectMapper());

    private PacketCodecFactory() {}

    /**
     * Compact CBOR Array Codec for high-frequency network traffic (UDP/TCP).
     * Serializes DTOs as compact, ordered CBOR arrays without string keys.
     */
    public static NetworkPacketCodec getCompactCborCodec() {
        return COMPACT_CBOR_CODEC;
    }

    /**
     * Standard Key-Value CBOR Codec.
     * Preserves field names as CBOR string keys.
     */
    public static NetworkPacketCodec getKeyValueCborCodec() {
        return KEY_VALUE_CBOR_CODEC;
    }

    /**
     * Plain-text Key-Value JSON Codec.
     * Useful for human inspection and debugging.
     */
    public static NetworkPacketCodec getKeyValueJsonCodec() {
        return KEY_VALUE_JSON_CODEC;
    }
}