package it.unibo.controller.server.network.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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

public class NettyGameNetworkServer implements PacketSender {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameNetworkServer.class);

    private final int tcpPort;
    private final int udpPort;

    // 1 thread dedicated strictly to TCP handshakes
    private final EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
    // Shared pool for active TCP/UDP input handling
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    private final NettyCborUdpPacketEncoder udpEncoder = new NettyCborUdpPacketEncoder();
    private final NettyCborTcpPacketEncoder tcpEncoder = new NettyCborTcpPacketEncoder();

    private final Map<PacketType, TcpPacketHandler> tcpHandlers = new EnumMap<>(PacketType.class);
    private final Map<PacketType, UdpPacketHandler> udpHandlers = new EnumMap<>(PacketType.class);

    private Channel tcpChannel;
    private Channel udpChannel;

    private final GameSessionRegistry sessions;

    public NettyGameNetworkServer(int tcpPort, int udpPort, GameSessionRegistry sessions) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.sessions = sessions;
    }

    public void addTcpHandler(PacketType type, TcpPacketHandler handler) {
        this.tcpHandlers.put(type, handler);
    }

    public void addUdpHandler(PacketType type, UdpPacketHandler handler) {
        this.udpHandlers.put(type, handler);
    }

    public void start() throws InterruptedException {
        Bootstrap udpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel.class)
                .handler(new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(DatagramChannel ch) {
                        ch.pipeline().addLast(
                                udpEncoder,
                                new SimpleChannelInboundHandler<DatagramPacket>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                                        handleIncomingUdp(msg);
                                    }
                                });
                    }
                });

        ServerBootstrap tcpBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                // Reads a 4 byte long header and waits until the full packet arrives
                                new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4),
                                // Appends a 4 byte integer to all outbound payloads
                                new LengthFieldPrepender(4),
                                // Handles network packet serialization and writing
                                tcpEncoder,
                                // Handles the reconstructed packet without the header
                                new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                        handleIncomingTcp(ctx.channel(), msg);
                                    }
                                }
                        );
                    }
                });
        this.udpChannel = udpBootstrap.bind(udpPort).sync().channel();
        this.tcpChannel = tcpBootstrap.bind(tcpPort).sync().channel();
        logger.info("TCP/UDP server is now running. TCP Port: {}, UDP Port: {}", tcpPort, udpPort);
    }

    public void stop() {
        tcpChannel.close();
        udpChannel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("TCP/UDP server is shutting down");
    }

    @Override
    public void sendTcp(String username, NetworkPacket packet) {
        Channel channel = sessions.get(username).getTcpChannel();
        channel.writeAndFlush(packet);
    }

    @Override
    public void sendUdp(String username, NetworkPacket packet) {
        GameUserSession session = sessions.get(username);
        InetSocketAddress udpAddress = session.getUdpAddress();
        udpChannel.writeAndFlush(new DefaultAddressedEnvelope<>(packet, udpAddress));
    }

    @Override
    public void broadcastTcp(NetworkPacket packet) {
        for (GameUserSession session : sessions.all()) {
            Channel channel = session.getTcpChannel();
            channel.writeAndFlush(packet);
        }
    }

    @Override
    public void broadcastUdp(NetworkPacket packet) {
        for (GameUserSession session : sessions.all()) {
            InetSocketAddress udpAddress = session.getUdpAddress();
            udpChannel.writeAndFlush(new DefaultAddressedEnvelope<>(packet, udpAddress));
        }
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