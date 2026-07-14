package it.unibo.controller.client.network.sockets.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import it.unibo.controller.shared.network.sockets.channel.NetworkConfig;
import it.unibo.controller.shared.network.sockets.codec.CborTcpPacketDecoder;
import it.unibo.controller.shared.network.sockets.codec.CborTcpPacketEncoder;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.PacketType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final Map<PacketType, TcpHandler> handlers;

    public GameClientChannelInitializer(Map<PacketType, TcpHandler> handlers) {
        this.handlers = handlers;
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
                new IdleStateHandler(
                        NetworkConfig.CLIENT_READ_TIMEOUT_SECONDS,
                        NetworkConfig.CLIENT_WRITE_PING_INTERVAL_SECONDS,
                        0,
                        TimeUnit.SECONDS),
                new GameClientNetworkHandler(handlers)
        );
    }
}