package it.unibo.controller.client.network;

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.packets.JoinMatchPacket;
import it.unibo.controller.shared.network.packets.PacketType;
import it.unibo.controller.shared.network.packets.UdpHandshakePacket;
import it.unibo.controller.shared.network.translation.GameContextDecoder;
import it.unibo.controller.shared.network.translation.GameContextDecoderImpl;
import it.unibo.model.entities.SpeculativeEntityFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class NettyGameNetworkClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameNetworkClient.class);
    private static final int TIMEOUT_IN_MILLIS = 5000;

    private final CBORMapper cborMapper = new CBORMapper();
    private final GameContextDecoder decoder = new GameContextDecoderImpl(new SpeculativeEntityFactoryImpl());
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    private final Channel tcpChannel;
    private final Channel udpChannel;
    private final InetSocketAddress serverUdpAddress;

    private final GameClientNetworkListener listener;

    private final String username;

    public NettyGameNetworkClient(String host, int tcpPort, int udpPort, String username,
                                   GameClientNetworkListener listener) throws InterruptedException {
        this.serverUdpAddress = new InetSocketAddress(host, udpPort);
        this.listener = listener;
        this.username = username;

        Bootstrap udpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                        handleIncomingPayload(msg.content());
                    }
                });
        this.udpChannel = udpBootstrap.bind(0).sync().channel();

        Bootstrap tcpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_IN_MILLIS)
                .option(ChannelOption.TCP_NODELAY, true) // Disable Nagle's algorithm
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4),
                                new LengthFieldPrepender(4),
                                new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                        handleIncomingPayload(msg);
                                    }
                                }
                        );
                    }
                });
        this.tcpChannel = tcpBootstrap.connect(host, tcpPort).sync().channel();
        logger.info("TCP connected to {}:{}", host, tcpPort);
        sendTcp(PacketType.JOIN_MATCH, new JoinMatchPacket(username));
        logger.info("Sent JOIN_MATCH for user '{}'", username);
    }

    public void sendTcp(PacketType type, Object packet) {
        if (tcpChannel == null || !tcpChannel.isActive()) {
            logger.warn("Cannot send TCP packet; channel is inactive.");
            return;
        }
        try {
            ByteBuf buffer = serializePacket(type, packet);
            tcpChannel.writeAndFlush(buffer);
        } catch (IOException e) {
            logger.error("Failed to send TCP packet of type: {}", type, e);
        }
    }

    public void sendUdp(PacketType type, Object packet) {
        if (udpChannel == null || !udpChannel.isActive()) {
            logger.warn("Cannot send UDP packet; channel is inactive.");
            return;
        }
        try {
            ByteBuf buffer = serializePacket(type, packet);
            udpChannel.writeAndFlush(new DatagramPacket(buffer, serverUdpAddress));
        } catch (IOException e) {
            logger.error("Failed to send UDP packet of type: {}", type, e);
        }
    }

    private void handleIncomingPayload(ByteBuf buffer) {
        if (buffer.readableBytes() < 1) return;
        byte packetTypeId = buffer.readByte();
        PacketType type = PacketType.fromId(packetTypeId);
        if (type == PacketType.JOIN_ACK) {
            sendUdp(PacketType.UDP_HANDSHAKE, new UdpHandshakePacket(username));
            logger.info("Received JOIN_ACK; sent UDP_HANDSHAKE for user '{}'", username);
            return;
        }
        if (type == PacketType.GAME_CONTEXT) {
            try (ByteBufInputStream inputStream = new ByteBufInputStream(buffer)) {
                GameContextDTO dto = cborMapper.readValue((InputStream) inputStream, GameContextDTO.class);
                listener.onGameContext(decoder.decode(dto));
            } catch (IOException e) {
                logger.error("Failed to decode payload", e);
            }
            return;
        }
        if (type == PacketType.GAME_START) {
            listener.onGameStart();
            return;
        }
        logger.warn("Unexpected or unhandled packet type received on client: {}", type);
    }

    private ByteBuf serializePacket(PacketType type, Object packet) throws IOException {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(type.getId());
        try (ByteBufOutputStream outputStream = new ByteBufOutputStream(buffer)) {
            cborMapper.writeValue((OutputStream) outputStream, packet);
        }
        return buffer;
    }
}