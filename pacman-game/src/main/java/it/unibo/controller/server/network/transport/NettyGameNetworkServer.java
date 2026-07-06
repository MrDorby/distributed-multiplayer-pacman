package it.unibo.controller.server.network.transport;

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
import it.unibo.controller.server.network.transport.handler.TcpPacketHandler;
import it.unibo.controller.server.network.transport.handler.UdpPacketHandler;
import it.unibo.controller.shared.network.packets.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.EnumMap;
import java.util.Map;

public class NettyGameNetworkServer implements PacketSender {
    private static final Logger logger = LoggerFactory.getLogger(NettyGameNetworkServer.class);

    private final int tcpPort;
    private final int udpPort;
    private final CBORMapper cborMapper = new CBORMapper();

    private final GameSessionRegistry sessions;
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    private final Map<PacketType, TcpPacketHandler> tcpHandlers = new EnumMap<>(PacketType.class);
    private final Map<PacketType, UdpPacketHandler> udpHandlers = new EnumMap<>(PacketType.class);

    private Channel tcpChannel;
    private Channel udpChannel;

    public NettyGameNetworkServer(int tcpPort, int udpPort, GameSessionRegistry sessions) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.sessions = sessions;
    }

    public void registerTcpHandler(PacketType type, TcpPacketHandler handler) {
        this.tcpHandlers.put(type, handler);
    }

    public void registerUdpHandler(PacketType type, UdpPacketHandler handler) {
        this.udpHandlers.put(type, handler);
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
        this.udpChannel = udpBootstrap.bind(udpPort).sync().channel();

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
        this.tcpChannel = tcpBootstrap.bind(tcpPort).sync().channel();
        logger.info("Server running. TCP Port: {}, UDP Port: {}", tcpPort, udpPort);
    }

    public void shutdown() {
        if (tcpChannel != null) {
            tcpChannel.close();
        }
        if (udpChannel != null) {
            udpChannel.close();
        }
        workerGroup.shutdownGracefully();
        logger.info("TCP/UDP server shut down. TCP Port: {}, UDP Port: {}", tcpPort, udpPort);
    }

    @Override
    public void sendTcp(String username, byte packetTypeId, Object packet) {
        GameUserSession session = sessions.get(username);
        if (session != null && session.getTcpChannel().isActive()) {
            try {
                session.getTcpChannel().writeAndFlush(serialize(packetTypeId, packet));
            } catch (IOException e) {
                logger.error("TCP send failed for user: {}", username, e);
            }
        }
    }

    @Override
    public void sendUdp(String username, byte packetTypeId, Object packet) {
        GameUserSession session = sessions.get(username);
        if (session != null && session.getUdpAddress() != null && udpChannel != null && udpChannel.isActive()) {
            try {
                ByteBuf buf = serialize(packetTypeId, packet);
                udpChannel.writeAndFlush(new DatagramPacket(buf, session.getUdpAddress()));
            } catch (IOException e) {
                logger.error("UDP send failed for user: {}", username, e);
            }
        }
    }

    @Override
    public void broadcastTcp(byte packetTypeId, Object packet) {
        try {
            ByteBuf buf = serialize(packetTypeId, packet);
            for (GameUserSession session : sessions.all()) {
                Channel channel = session.getTcpChannel();
                if (channel != null && channel.isActive()) {
                    channel.writeAndFlush(buf.retainedDuplicate());
                }
            }
            buf.release();
        } catch (IOException e) {
            logger.error("TCP broadcast failed", e);
        }
    }

    @Override
    public void broadcastUdp(byte packetTypeId, Object packet) {
        try {
            ByteBuf buf = serialize(packetTypeId, packet);
            for (GameUserSession session : sessions.all()) {
                InetSocketAddress udpAddr = session.getUdpAddress();
                if (udpAddr != null && udpChannel != null && udpChannel.isActive()) {
                    udpChannel.writeAndFlush(new DatagramPacket(buf.retainedDuplicate(), udpAddr));
                }
            }
            buf.release();
        } catch (IOException e) {
            logger.error("UDP broadcast failed", e);
        }
    }

    private void handleIncomingTcp(Channel channel, ByteBuf buffer) {
        PacketType type;
        try {
            byte packetTypeId = buffer.readByte();
            type = PacketType.fromId(packetTypeId);
        } catch (Exception e) {
            logger.warn("Received malformed/unrecognized TCP packet.", e);
            return;
        }
        TcpPacketHandler handler = tcpHandlers.get(type);
        if (handler == null) {
            logger.warn("Received unhandled TCP packet type: {}", type);
            return;
        }
        try (ByteBufInputStream inputStream = new ByteBufInputStream(buffer)) {
            handler.handle(channel, inputStream);
        } catch (IOException e) {
            logger.error("Failed to unpack TCP payload for {}", type, e);
        }
    }

    private void handleIncomingUdp(DatagramPacket datagram) {
        ByteBuf buffer = datagram.content();
        PacketType type;
        try {
            byte packetTypeId = buffer.readByte();
            type = PacketType.fromId(packetTypeId);
        } catch (Exception e) {
            logger.warn("Received malformed/unrecognized UDP packet from {}.", datagram.sender(), e);
            return;
        }
        UdpPacketHandler handler = udpHandlers.get(type);
        if (handler == null) {
            logger.warn("Received unhandled UDP packet type: {}", type);
            return;
        }
        try (ByteBufInputStream is = new ByteBufInputStream(buffer)) {
            handler.handle(datagram.sender(), is);
        } catch (IOException e) {
            logger.error("Failed to unpack UDP payload for {}", type, e);
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