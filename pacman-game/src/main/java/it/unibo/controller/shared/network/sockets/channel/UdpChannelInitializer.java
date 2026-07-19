package it.unibo.controller.shared.network.sockets.channel;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import it.unibo.controller.shared.network.sockets.handlers.UdpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import it.unibo.controller.shared.network.sockets.codec.CborUdpPacketDecoder;
import it.unibo.controller.shared.network.sockets.codec.CborUdpPacketEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

public class UdpChannelInitializer extends ChannelInitializer<DatagramChannel> {
    private static final Logger logger = LoggerFactory.getLogger(UdpChannelInitializer.class);
    private final Map<PacketType, UdpHandler> udpHandlers;

    public UdpChannelInitializer(Map<PacketType, UdpHandler> udpHandlers) {
        this.udpHandlers = udpHandlers;
    }

    @Override
    protected void initChannel(DatagramChannel ch) {
        ch.pipeline().addLast(
                // Serializes a NetworkPacket into CBOR and wraps it in a DatagramPacket for transmission.
                new CborUdpPacketEncoder(),
                // Deserializes an incoming DatagramPacket into NetworkPacket while keeping the sender and recipient addresses.
                new CborUdpPacketDecoder(),
                // Receives fully decoded NetworkPacket objects and routes them to the appropriate handler.
                new SimpleChannelInboundHandler<AddressedEnvelope<NetworkPacket, InetSocketAddress>>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, AddressedEnvelope<NetworkPacket, InetSocketAddress> msg) {
                        NetworkPacket packet = msg.content();
                        UdpHandler handler = udpHandlers.get(packet.getType());
                        if (handler != null) {
                            handler.handle(msg.sender(), packet);
                        } else {
                            logger.debug("No UDP handler registered for {}", packet.getType());
                        }
                    }
                }
        );
    }
}
