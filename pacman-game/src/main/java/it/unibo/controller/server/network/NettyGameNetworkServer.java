package it.unibo.controller.server.network;

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.packets.JoinMatchPacket;
import it.unibo.controller.shared.network.packets.PacketType;
import it.unibo.controller.shared.network.packets.UdpHandshakePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyGameNetworkServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameNetworkServer.class);
    private final int tcpPort;
    private final int udpPort;
    private final CBORMapper cborMapper = new CBORMapper();
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    private final Map<String, GameUserSession> activeSessions = new ConcurrentHashMap<>();
    private Channel serverTcpChannel;
    private Channel serverUdpChannel;

    private final GameServerNetworkListener listener;

    public NettyGameNetworkServer(int tcpPort, int udpPort, GameServerNetworkListener listener) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.listener = listener;
    }

    public void start() throws Exception {
        Bootstrap udpBootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel.class)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                        handleIncomingUdp(msg);
                    }
                });
        this.serverUdpChannel = udpBootstrap.bind(udpPort).sync().channel();

        ServerBootstrap tcpBootstrap = new ServerBootstrap()
                .group(workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4),
                                new LengthFieldPrepender(4),
                                new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                        handleIncomingTcp(ctx.channel(), msg);
                                    }
                                }
                        );
                    }
                });
        this.serverTcpChannel = tcpBootstrap.bind(tcpPort).sync().channel();
        logger.info("Server running. TCP Port: {}, UDP Port: {}", tcpPort, udpPort);
    }

    public void sendTcp(String username, byte packetTypeId, Object packet) {
        GameUserSession session = activeSessions.get(username);
        if (session != null && session.getTcpChannel().isActive()) {
            try {
                session.getTcpChannel().writeAndFlush(serialize(packetTypeId, packet));
            } catch (IOException e) {
                logger.error("TCP send failed for user: {}", username, e);
            }
        }
    }

    public void sendUdp(String username, byte packetTypeId, Object packet) {
        GameUserSession session = activeSessions.get(username);
        if (session != null && session.getUdpAddress() != null && serverUdpChannel != null && serverUdpChannel.isActive()) {
            try {
                ByteBuf buf = serialize(packetTypeId, packet);
                serverUdpChannel.writeAndFlush(new DatagramPacket(buf, session.getUdpAddress()));
            } catch (IOException e) {
                logger.error("UDP send failed for user: {}", username, e);
            }
        }
    }

    public void broadcastTcp(byte packetTypeId, Object packet) {
        try {
            ByteBuf buf = serialize(packetTypeId, packet);
            for (GameUserSession session : activeSessions.values()) {
                Channel channel = session.getTcpChannel();
                if (channel != null && channel.isActive()) {
                    channel.writeAndFlush(buf.retain());
                }
            }
            buf.release();
        } catch (IOException e) {
            logger.error("TCP broadcast failed", e);
        }
    }

    public void broadcastUdp(byte packetTypeId, Object packet) {
        try {
            ByteBuf buf = serialize(packetTypeId, packet);
            for (GameUserSession session : activeSessions.values()) {
                InetSocketAddress udpAddr = session.getUdpAddress();
                if (udpAddr != null && serverUdpChannel != null && serverUdpChannel.isActive()) {
                    serverUdpChannel.writeAndFlush(new DatagramPacket(buf.retain(), udpAddr));
                }
            }
            buf.release();
        } catch (IOException e) {
            logger.error("UDP broadcast failed", e);
        }
    }

    private void handleIncomingTcp(Channel channel, ByteBuf buffer) {
        try {
            byte packetTypeId = buffer.readByte();
            PacketType type = PacketType.fromId(packetTypeId);
            if (type == PacketType.JOIN_MATCH) {
                try (ByteBufInputStream is = new ByteBufInputStream(buffer)) {
                    JoinMatchPacket packet = cborMapper.readValue((InputStream) is, JoinMatchPacket.class);
                    String username = packet.username();
                    GameUserSession session = new GameUserSession(username, channel);
                    activeSessions.put(username, session);
                    logger.info("Player {} successfully connected via TCP.", username);
                    listener.onPlayerJoined(username);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to unpack TCP routing payload", e);
        }
    }

    private void handleIncomingUdp(DatagramPacket datagram) {
        ByteBuf buffer = datagram.content();
        try {
            byte packetTypeId = buffer.readByte();
            PacketType type = PacketType.fromId(packetTypeId);
            if (type == PacketType.UDP_HANDSHAKE) {
                try (ByteBufInputStream inputStream = new ByteBufInputStream(buffer)) {
                    UdpHandshakePacket handshake = cborMapper.readValue((InputStream) inputStream, UdpHandshakePacket.class);
                    GameUserSession session = activeSessions.get(handshake.username());
                    if (session != null) {
                        session.setUdpAddress(datagram.sender());
                        logger.info("Player {} UDP bound to real remote endpoint: {}", handshake.username(), datagram.sender());
                    }
                }
                return;
            }
            if (type == PacketType.MOVE_COMMAND) {
                try (ByteBufInputStream inputStream = new ByteBufInputStream(buffer)) {
                    PacmanMoveCommand command = cborMapper.readValue((InputStream) inputStream, PacmanMoveCommand.class);
                    String senderId = command.pacmanId();
                    GameUserSession session = activeSessions.get(senderId);
                    if (session != null && datagram.sender().equals(session.getUdpAddress())) {
                        listener.onCommandReceived(senderId, command);
                    } else {
                        logger.warn("Intercepted bad UDP packet from: {}", datagram.sender());
                    }
                }
                return;
            }
            logger.warn("Received unhandled UDP packet type: {}", type);
        } catch (IOException e) {
            logger.error("Server failed to unpack UDP routing payload", e);
        }
    }

    private ByteBuf serialize(byte packetTypeId, Object packet) throws IOException {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(packetTypeId);
        try (ByteBufOutputStream outputStream = new ByteBufOutputStream(buffer)) {
            cborMapper.writeValue((OutputStream) outputStream, packet);
        }
        return buffer;
    }
}