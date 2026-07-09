package it.unibo.controller.server.network.sockets;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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

public class NettyGameNetworkServer implements GameNetworkServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameNetworkServer.class);

    private final int tcpPort;
    private final int udpPort;

    // 1 thread dedicated strictly to TCP handshakes
    private final EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
    // Shared pool for active TCP/UDP input handling
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    private final Map<PacketType, TcpHandler> tcpHandlers = new EnumMap<>(PacketType.class);
    private final Map<PacketType, UdpHandler> udpHandlers = new EnumMap<>(PacketType.class);

    private Channel tcpChannel;
    private Channel udpChannel;
    private final GameSessionRegistry sessions;

    public NettyGameNetworkServer(int tcpPort, int udpPort, GameSessionRegistry sessions) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.sessions = sessions;
    }

    @Override
    public void addTcpHandler(PacketType type, TcpHandler handler) {
        this.tcpHandlers.put(type, handler);
    }

    @Override
    public void addUdpHandler(PacketType type, UdpHandler handler) {
        this.udpHandlers.put(type, handler);
    }

    @Override
    public void start() throws InterruptedException {
        Bootstrap udpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel.class)
                .handler(new UdpChannelInitializer(udpHandlers));

        ServerBootstrap tcpBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new TcpChannelInitializer(tcpHandlers));

        this.udpChannel = udpBootstrap.bind(udpPort).sync().channel();
        this.tcpChannel = tcpBootstrap.bind(tcpPort).sync().channel();
        logger.info("TCP/UDP server is now running. TCP Port: {}, UDP Port: {}", tcpPort, udpPort);
    }

    @Override
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
}