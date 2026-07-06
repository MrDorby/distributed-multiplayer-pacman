package it.unibo.controller.server.network.transport.handler;

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import it.unibo.controller.server.GameServerNetworkListener;
import it.unibo.controller.server.network.transport.GameSessionRegistry;
import it.unibo.controller.server.network.transport.PacketSender;
import it.unibo.controller.shared.network.packets.JoinAckPacket;
import it.unibo.controller.shared.network.packets.JoinMatchPacket;
import it.unibo.controller.shared.network.packets.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class JoinMatchHandler implements TcpPacketHandler {
    private static final Logger logger = LoggerFactory.getLogger(JoinMatchHandler.class);

    private final CBORMapper cborMapper = new CBORMapper();
    private final GameSessionRegistry sessions;
    private final GameServerNetworkListener listener;
    private final PacketSender sender;

    public JoinMatchHandler(GameSessionRegistry sessions, GameServerNetworkListener listener, PacketSender sender) {
        this.sessions = sessions;
        this.listener = listener;
        this.sender = sender;
    }

    @Override
    public void handle(Channel channel, ByteBufInputStream payload) throws IOException {
        JoinMatchPacket packet = cborMapper.readValue((InputStream) payload, JoinMatchPacket.class);
        String username = packet.username();
        sessions.register(username, channel);
        logger.info("Player {} successfully connected via TCP.", username);
        sender.sendTcp(username, PacketType.JOIN_ACK.getId(), new JoinAckPacket());
        logger.info("Sent JOIN_ACK to {}", username);
        listener.onPlayerJoined(username);
    }
}
