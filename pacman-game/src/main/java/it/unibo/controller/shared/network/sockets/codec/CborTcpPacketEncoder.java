package it.unibo.controller.shared.network.sockets.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class CborTcpPacketEncoder extends MessageToByteEncoder<NetworkPacket> {
    private static final Logger logger = LoggerFactory.getLogger(CborTcpPacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, NetworkPacket packet, ByteBuf out) throws IOException {
        try {
            CborPacketSerializer.serialize(packet, out);
        } catch (IOException e) {
            logger.error("Failed to serialize TCP packet destined for {}", ctx.channel().remoteAddress(), e);
            throw e;
        }
    }
}