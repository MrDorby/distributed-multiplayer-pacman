package it.unibo.controller.shared.network.sockets.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import it.unibo.controller.shared.network.sockets.codec.CborTcpPacketDecoder;
import it.unibo.controller.shared.network.sockets.codec.CborTcpPacketEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(TcpChannelInitializer.class);
    private final Map<PacketType, TcpHandler> tcpHandlers;

    public TcpChannelInitializer(Map<PacketType, TcpHandler> tcpHandlers) {
        this.tcpHandlers = tcpHandlers;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                // This decoder expects each message to begin with a 4-byte length field: [length][payload]
                // After decoding, handlers receive only the payload bytes.
                new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4),
                // Performs the opposite operation for outgoing messages.
                new LengthFieldPrepender(4),
                // Converts a NetworkPacket object into CBOR-encoded bytes before the message is sent across the network.
                new CborTcpPacketEncoder(),
                // Converts a CBOR payload received from the network into a NetworkPacket instance.
                new CborTcpPacketDecoder(),
                // Receives fully decoded NetworkPacket objects and routes them to the appropriate handler.
                new SimpleChannelInboundHandler<NetworkPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, NetworkPacket packet) {
                        TcpHandler handler = tcpHandlers.get(packet.getType());
                        if (handler != null) {
                            handler.handle(ctx.channel(), packet);
                        } else {
                            logger.debug("No TCP handler registered for {}", packet.getType());
                        }
                    }
                }
        );
    }
}
