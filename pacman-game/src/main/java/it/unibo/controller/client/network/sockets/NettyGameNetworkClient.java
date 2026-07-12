package it.unibo.controller.client.network.sockets;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import it.unibo.controller.shared.network.sockets.channel.TcpChannelInitializer;
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

public class NettyGameNetworkClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameNetworkClient.class);
    private static final int TIMEOUT_IN_MILLIS = 5000;

    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    private final Map<PacketType, TcpHandler> tcpHandlers = new EnumMap<>(PacketType.class);
    private final Map<PacketType, UdpHandler> udpHandlers = new EnumMap<>(PacketType.class);

    private final int tcpPort;
    private final int udpPort;
    private final InetSocketAddress serverAddress;

    private Channel tcpChannel;
    private Channel udpChannel;

    public void addTcpHandler(PacketType type, TcpHandler handler) {
        this.tcpHandlers.put(type, handler);
    }

    public void addUdpHandler(PacketType type, UdpHandler handler) {
        this.udpHandlers.put(type, handler);
    }

    public NettyGameNetworkClient(String host, int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.serverAddress = new InetSocketAddress(host, tcpPort);
    }

    public void start() throws InterruptedException {
        Bootstrap udpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.RECVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
                .handler(new UdpChannelInitializer(udpHandlers));

        Bootstrap tcpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_IN_MILLIS)
                .option(ChannelOption.TCP_NODELAY, true) // Disable Nagle's algorithm
                .handler(new TcpChannelInitializer(tcpHandlers));

        this.udpChannel = udpBootstrap.bind(0).sync().channel();
        this.tcpChannel = tcpBootstrap.connect(serverAddress).sync().channel();
        logger.info("UDP listener running on port {}. TCP connection established with {}", udpPort, serverAddress);
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
        udpChannel.writeAndFlush(new DefaultAddressedEnvelope<>(packet, serverAddress));
    }
}