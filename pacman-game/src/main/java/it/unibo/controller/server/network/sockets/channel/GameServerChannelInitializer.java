package it.unibo.controller.server.network.sockets.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import it.unibo.controller.server.network.sockets.session.GameSessionController;
import it.unibo.controller.shared.network.sockets.channel.NetworkConfig;
import it.unibo.controller.shared.network.sockets.codec.CborTcpPacketDecoder;
import it.unibo.controller.shared.network.sockets.codec.CborTcpPacketEncoder;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.PacketType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final Map<PacketType, TcpHandler> tcpHandlers;
    private final GameSessionController sessionController;

    public GameServerChannelInitializer(Map<PacketType, TcpHandler> tcpHandlers, GameSessionController sessionController) {
        this.tcpHandlers = tcpHandlers;
        this.sessionController = sessionController;
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
                new IdleStateHandler(NetworkConfig.SERVER_READ_TIMEOUT_SECONDS, 0, 0, TimeUnit.SECONDS),
                new GameServerNetworkHandler(tcpHandlers, sessionController)
        );
    }
}