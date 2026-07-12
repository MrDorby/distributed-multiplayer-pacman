package it.unibo.controller.client.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.client.network.sockets.NettyGameNetworkClient;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.JoinGameAckPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.UdpHandshakePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinAckHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(JoinAckHandler.class);
    private final NettyGameNetworkClient client;

    public JoinAckHandler(NettyGameNetworkClient client) {
        this.client = client;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        JoinGameAckPacket ackPacket = (JoinGameAckPacket) packet;
        logger.debug("Received {} over TCP from {}", ackPacket.getType(), channel.remoteAddress());
        String token = ackPacket.token();
        NetworkPacket udpHandshakePacket = new UdpHandshakePacket(token);
        logger.debug("Sending {} over UDP with token {} to {}", udpHandshakePacket.getType(), token, channel.remoteAddress());
        client.sendUdp(new UdpHandshakePacket(token));
    }
}
