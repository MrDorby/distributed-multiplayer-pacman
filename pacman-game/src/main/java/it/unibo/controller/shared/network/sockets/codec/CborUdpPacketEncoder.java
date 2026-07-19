package it.unibo.controller.shared.network.sockets.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class CborUdpPacketEncoder extends MessageToMessageEncoder<AddressedEnvelope<NetworkPacket, InetSocketAddress>> {
    private static final Logger logger = LoggerFactory.getLogger(CborUdpPacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<NetworkPacket, InetSocketAddress> msg, List<Object> out) throws IOException {
        ByteBuf buffer = ctx.alloc().buffer();
        try {
            CborPacketSerializer.serialize(msg.content(), buffer);
            out.add(new DatagramPacket(buffer, msg.recipient()));
        } catch (IOException e) {
            logger.warn("Failed to serialize UDP packet destined for {}", msg.recipient());
            buffer.release();
            throw e;
        }
    }
}
