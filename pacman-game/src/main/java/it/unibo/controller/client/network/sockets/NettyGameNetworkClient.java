package it.unibo.controller.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import it.unibo.controller.shared.network.TcpPacketHandler;
import it.unibo.controller.shared.network.UdpPacketHandler;
import it.unibo.controller.shared.network.NettyCborTcpPacketEncoder;
import it.unibo.controller.shared.network.NettyCborUdpPacketEncoder;
import it.unibo.controller.shared.network.packets.NetworkPacket;
import it.unibo.controller.shared.network.packets.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.EnumMap;
import java.util.Map;

public class NettyGameNetworkClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameNetworkClient.class);
    private static final int TIMEOUT_IN_MILLIS = 5000;

    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    private final NettyCborUdpPacketEncoder udpEncoder = new NettyCborUdpPacketEncoder();
    private final NettyCborTcpPacketEncoder tcpEncoder = new NettyCborTcpPacketEncoder();

    private final Map<PacketType, TcpPacketHandler> tcpHandlers = new EnumMap<>(PacketType.class);
    private final Map<PacketType, UdpPacketHandler> udpHandlers = new EnumMap<>(PacketType.class);

    private final int tcpPort;
    private final int udpPort;

    private final InetSocketAddress serverUdpAddress;

    private Channel tcpChannel;
    private Channel udpChannel;

    public void addTcpHandler(PacketType type, TcpPacketHandler handler) {
        this.tcpHandlers.put(type, handler);
    }

    public void addUdpHandler(PacketType type, UdpPacketHandler handler) {
        this.udpHandlers.put(type, handler);
    }

    public NettyGameNetworkClient(String host, int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.serverUdpAddress = new InetSocketAddress(host, tcpPort);
    }

    public void start() throws InterruptedException {
        Bootstrap udpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.RECVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
                .handler(new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(DatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                udpEncoder,
                                new SimpleChannelInboundHandler<DatagramPacket>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                                        handleIncomingUdp(msg);
                                    }
                                }
                        );
                    }
                });

        Bootstrap tcpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_IN_MILLIS)
                .option(ChannelOption.TCP_NODELAY, true) // Disable Nagle's algorithm
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                // Reads a 4 byte long header and waits until the full packet arrives
                                new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4),
                                // Appends a 4 byte integer to all outbound payloads
                                new LengthFieldPrepender(4),
                                // Handles the reconstructed packet without the header
                                tcpEncoder,
                                new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                        handleIncomingTcp(ctx.channel(), msg);
                                    }
                                }
                        );
                    }
                });
        this.udpChannel = udpBootstrap.bind(0).sync().channel();
        this.tcpChannel = tcpBootstrap.connect(serverUdpAddress).sync().channel();
        logger.info("UDP listener running on port {}. TCP connection established with {}", udpPort, serverUdpAddress);
    }

    public void stop() {
        tcpChannel.close();
        udpChannel.close();
        workerGroup.shutdownGracefully();
        logger.info("Closing TCP and UDP port connections");
    }

    public void sendTcp(NetworkPacket packet) {
        tcpChannel.writeAndFlush(packet);
    }

    public void sendUdp(NetworkPacket packet) {
        udpChannel.writeAndFlush(new DefaultAddressedEnvelope<>(packet, serverUdpAddress));
    }

    private void handleIncomingTcp(Channel channel, ByteBuf buffer) {
        try {
            byte packetTypeId = buffer.readByte();
            PacketType type = PacketType.fromId(packetTypeId);
            TcpPacketHandler handler = tcpHandlers.get(type);
            if (handler == null) {
                logger.warn("Received unhandled TCP packet type: {} from {}", type, channel.remoteAddress());
                return;
            }
            try (ByteBufInputStream inputStream = new ByteBufInputStream(buffer)) {
                handler.handle(channel, inputStream);
            }
        } catch (IOException e) {
            logger.warn("Failed to process incoming TCP packet from {}", channel.remoteAddress(), e);
            channel.close();
        }
    }

    private void handleIncomingUdp(DatagramPacket datagram) {
        ByteBuf buffer = datagram.content();
        try {
            byte packetTypeId = buffer.readByte();
            PacketType type = PacketType.fromId(packetTypeId);
            UdpPacketHandler handler = udpHandlers.get(type);
            if (handler == null) {
                logger.warn("Received unhandled UDP packet type: {} from {}", type, datagram.sender());
                return;
            }
            try (ByteBufInputStream is = new ByteBufInputStream(buffer)) {
                handler.handle(datagram.sender(), is);
            }
        } catch (Exception e) {
            logger.error("Failed to process incoming UDP packet from {}", datagram.sender(), e);
        }
    }
}