package it.unibo.controller.shared.network.sockets.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class CborTcpPacketDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(CborTcpPacketDecoder.class);
    private final NetworkPacketCodec codec = PacketCodecFactory.getCompactCborCodec();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException {
        try {
            out.add(codec.decode(in));
        } catch (IOException e) {
            logger.warn("Bad TCP stream from {}", ctx.channel().remoteAddress());
            throw e;
        }
    }
}