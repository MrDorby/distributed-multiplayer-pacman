package it.unibo.controller.shared.network.sockets.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class CborUdpPacketDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private static final Logger logger = LoggerFactory.getLogger(CborUdpPacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws IOException {
        try {
            NetworkPacket packet = CborPacketSerializer.deserialize(msg.content());
            out.add(new DefaultAddressedEnvelope<>(packet, msg.recipient(), msg.sender()));
        } catch (IOException e) {
            logger.warn("Received bad UDP packet from {}", msg.sender());
            throw e;
        }
    }
}
