package it.unibo.controller.client.network.sockets;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import it.unibo.controller.client.network.sockets.channel.GameClientChannelInitializer;
import it.unibo.controller.shared.network.sockets.channel.UdpChannelInitializer;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.handlers.UdpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.EnumMap;
import java.util.Map;

public class NettyGameClientGateway implements GameClientGateway {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameClientGateway.class);
    private static final int TIMEOUT_IN_MILLIS = 5000;
    public static final int MAX_UDP_PAYLOAD_SIZE_IN_BYTES = 65535; // TODO reduce later on once the max payload size is known

    // Shared pool for active TCP/UDP input handling
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    private final Map<PacketType, TcpHandler> tcpHandlers = new EnumMap<>(PacketType.class);
    private final Map<PacketType, UdpHandler> udpHandlers = new EnumMap<>(PacketType.class);

    private final int udpPort;
    private final InetSocketAddress remoteAddress;

    private Channel tcpChannel;
    private Channel udpChannel;

    @Override
    public void addTcpHandler(PacketType type, TcpHandler handler) {
        this.tcpHandlers.put(type, handler);
    }

    @Override
    public void addUdpHandler(PacketType type, UdpHandler handler) {
        this.udpHandlers.put(type, handler);
    }

    public NettyGameClientGateway(String host, int tcpPort, int udpPort) {
        this.udpPort = udpPort;
        this.remoteAddress = new InetSocketAddress(host, tcpPort);
    }

    @Override
    public void start() throws InterruptedException {
        Bootstrap udpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.RECVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(MAX_UDP_PAYLOAD_SIZE_IN_BYTES))
                .handler(new UdpChannelInitializer(udpHandlers));

        Bootstrap tcpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_IN_MILLIS)
                .option(ChannelOption.TCP_NODELAY, true) // Disables Nagle's algorithm
                .handler(new GameClientChannelInitializer(tcpHandlers));

        this.udpChannel = udpBootstrap.bind(0).sync().channel();

        try {
            this.tcpChannel = tcpBootstrap.connect(remoteAddress).sync().channel();
            logger.info("UDP port {} opened. TCP connection established with {}", udpPort, remoteAddress);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (Exception e) {
            logger.error("Failed to establish TCP connection to {}", remoteAddress, e);
            throw new RuntimeException("Could not connect to game server", e);
        }
    }

    @Override
    public void stop() {
        tcpChannel.close();
        udpChannel.close();
        workerGroup.shutdownGracefully();
        logger.info("Closing TCP and UDP connections");
    }

    @Override
    public void sendTcp(NetworkPacket packet) {
        tcpChannel.writeAndFlush(packet);
    }

    @Override
    public void sendUdp(NetworkPacket packet) {
        udpChannel.writeAndFlush(new DefaultAddressedEnvelope<>(packet, remoteAddress));
    }
}