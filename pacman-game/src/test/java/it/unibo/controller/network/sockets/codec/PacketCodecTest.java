package it.unibo.controller.network.sockets.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.sockets.codec.NetworkPacketCodec;
import it.unibo.controller.shared.network.sockets.codec.PacketCodecFactory;
import it.unibo.controller.shared.network.sockets.packets.GameContextPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.translation.GameContextEncoderImpl;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacketCodecTest {

    @Test
    void testPayloadSizes() throws Exception {
        GameContext context = GameContextFactory.createFromMap("maps/map1.json", new GameEntityFactoryImpl());
        GameContextDTO dto = new GameContextEncoderImpl().encode(context);
        NetworkPacket packet = new GameContextPacket(dto);
        NetworkPacketCodec jsonCodec = PacketCodecFactory.getKeyValueJsonCodec();
        NetworkPacketCodec keyValueCborCodec = PacketCodecFactory.getKeyValueCborCodec();
        NetworkPacketCodec compactCborCodec = PacketCodecFactory.getCompactCborCodec();
        ByteBuf jsonBuffer = Unpooled.buffer();
        ByteBuf keyValueCborBuffer = Unpooled.buffer();
        ByteBuf compactCborBuffer = Unpooled.buffer();
        try {
            jsonCodec.encode(packet, jsonBuffer);
            keyValueCborCodec.encode(packet, keyValueCborBuffer);
            compactCborCodec.encode(packet, compactCborBuffer);
            int jsonSize = jsonBuffer.readableBytes();
            int keyValueCborSize = keyValueCborBuffer.readableBytes();
            int compactCborSize = compactCborBuffer.readableBytes();
            System.out.println("JSON size:        " + jsonSize + " bytes");
            System.out.println("CBOR (Key-Value): " + keyValueCborSize + " bytes");
            System.out.println("CBOR (Compact):   " + compactCborSize + " bytes");
            assertTrue(keyValueCborSize < jsonSize, "Key-Value CBOR should be smaller than JSON");
            assertTrue(compactCborSize < keyValueCborSize, "Compact CBOR (Array) should be smaller than Key-Value CBOR");
        } finally {
            jsonBuffer.release();
            keyValueCborBuffer.release();
            compactCborBuffer.release();
        }
    }
}