package it.unibo.controller.server.network.transport.handler;

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import it.unibo.controller.server.GameServerNetworkListener;
import it.unibo.controller.server.network.transport.GameSessionRegistry;
import it.unibo.controller.server.network.transport.GameUserSession;
import it.unibo.controller.server.network.transport.PacketSender;
import it.unibo.controller.shared.network.TcpPacketHandler;
import it.unibo.controller.shared.network.packets.JoinGameAckPacket;
import it.unibo.controller.shared.network.packets.JoinGamePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class JoinGameHandler implements TcpPacketHandler {
    private static final Logger logger = LoggerFactory.getLogger(JoinGameHandler.class);

    private final CBORMapper cborMapper = new CBORMapper();
    private final GameSessionRegistry sessions;
    private final GameServerNetworkListener listener;
    private final PacketSender sender;

    public JoinGameHandler(GameSessionRegistry sessions, GameServerNetworkListener listener, PacketSender sender) {
        this.sessions = sessions;
        this.listener = listener;
        this.sender = sender;
    }

    @Override
    public void handle(Channel channel, ByteBufInputStream payload) throws IOException {
        JoinGamePacket packet = cborMapper.readValue((InputStream) payload, JoinGamePacket.class);
        String username = packet.username();
        GameUserSession session = sessions.register(username, channel);
        logger.info("Player {} successfully connected via TCP.", username);
        String token = UUID.randomUUID().toString();
        session.setUdpToken(token);
        sender.sendTcp(username, new JoinGameAckPacket(token));
        logger.info("Sent JOIN_ACK with token to {}", username);
        listener.onPlayerJoined(username);
    }
}
