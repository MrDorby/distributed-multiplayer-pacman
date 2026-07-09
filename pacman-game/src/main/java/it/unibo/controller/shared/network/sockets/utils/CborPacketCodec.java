package it.unibo.controller.shared.network.sockets.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class CborPacketCodec {
    private static final ObjectMapper CBOR_MAPPER = new CBORMapper();
    public static final TcpEncoder TCP_ENCODER = new TcpEncoder();
    public static final UdpEncoder UDP_ENCODER = new UdpEncoder();

    private CborPacketCodec() {}

    public static void serialize(NetworkPacket packet, ByteBuf buffer) throws IOException {
        buffer.writeByte(packet.getType().getId());
        try (ByteBufOutputStream outputStream = new ByteBufOutputStream(buffer)) {
            CBOR_MAPPER.writeValue((OutputStream) outputStream, packet);
        }
    }

    @ChannelHandler.Sharable
    public static final class TcpEncoder extends MessageToByteEncoder<NetworkPacket> {
        private TcpEncoder() {}

        @Override
        protected void encode(ChannelHandlerContext ctx, NetworkPacket packet, ByteBuf out) throws Exception {
            serialize(packet, out);
        }
    }

    @ChannelHandler.Sharable
    public static final class UdpEncoder extends MessageToMessageEncoder<AddressedEnvelope<NetworkPacket, InetSocketAddress>> {
        private UdpEncoder() {}

        @Override
        protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<NetworkPacket, InetSocketAddress> msg, List<Object> out) throws Exception {
            ByteBuf buf = ctx.alloc().buffer();
            try {
                serialize(msg.content(), buf);
                out.add(new DatagramPacket(buf, msg.recipient()));
            } catch (Throwable t) {
                buf.release();
                throw t;
            }
        }
    }
}
