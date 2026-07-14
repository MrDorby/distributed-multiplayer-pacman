package it.unibo.controller.server.network.sockets;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import it.unibo.controller.server.network.sockets.session.GameSessionController;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.server.network.sockets.channel.GameServerChannelInitializer;
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

public class NettyGameServerGateway implements GameServerGateway {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameServerGateway.class);

    // 1 thread dedicated strictly to TCP handshakes
    private final EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
    // Shared pool for active TCP/UDP input handling
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    private final Map<PacketType, TcpHandler> tcpHandlers = new EnumMap<>(PacketType.class);
    private final Map<PacketType, UdpHandler> udpHandlers = new EnumMap<>(PacketType.class);

    private final int tcpPort;
    private final int udpPort;
    private Channel tcpChannel;
    private Channel udpChannel;
    private final GameSessionController sessionController;

    public NettyGameServerGateway(int tcpPort, int udpPort, GameSessionController sessionController) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.sessionController = sessionController;
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
                .childHandler(new GameServerChannelInitializer(tcpHandlers, sessionController));

        this.udpChannel = udpBootstrap.bind(udpPort).sync().channel();
        this.tcpChannel = tcpBootstrap.bind(tcpPort).sync().channel();
        logger.info("TCP/UDP gateway is now running. TCP Port: {}, UDP Port: {}", tcpPort, udpPort);
    }

    @Override
    public void stop() {
        tcpChannel.close();
        udpChannel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("TCP/UDP gateway is shutting down");
    }

    @Override
    public void sendTcp(String username, NetworkPacket packet) {
        GameSession session = sessionController.getSessionByUsername(username);
        if (session == null) {
            logger.warn("Cannot send over TCP: no session for {}", username);
            return;
        }
        Channel channel = session.getTcpChannel();
        if (channel == null || !channel.isActive()) {
            logger.warn("Cannot send over TCP: {} has no active TCP channel", username);
            return;
        }
        channel.writeAndFlush(packet);
    }

    @Override
    public void sendUdp(String username, NetworkPacket packet) {
        GameSession session = sessionController.getSessionByUsername(username);
        if (session == null) {
            logger.warn("Cannot send over UDP: no session for {}", username);
            return;
        }
        InetSocketAddress udpAddress = session.getUdpAddress();
        if (udpAddress == null) {
            logger.warn("Cannot send over UDP: {} has no bound UDP address yet", username);
            return;
        }
        udpChannel.writeAndFlush(new DefaultAddressedEnvelope<>(packet, udpAddress));
    }

    @Override
    public void broadcastTcp(NetworkPacket packet) {
        for (GameSession session : sessionController.getAllSessions()) {
            Channel channel = session.getTcpChannel();
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(packet);
            }
        }
    }

    @Override
    public void broadcastUdp(NetworkPacket packet) {
        for (GameSession session : sessionController.getAllSessions()) {
            InetSocketAddress udpAddress = session.getUdpAddress();
            if (udpAddress != null) {
                udpChannel.writeAndFlush(new DefaultAddressedEnvelope<>(packet, udpAddress));
            }
        }
    }
}