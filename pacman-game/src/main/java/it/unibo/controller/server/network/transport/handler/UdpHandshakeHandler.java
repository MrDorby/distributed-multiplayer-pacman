package it.unibo.controller.server.network.transport.handler;

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.netty.buffer.ByteBufInputStream;
import it.unibo.controller.server.network.transport.GameSessionRegistry;
import it.unibo.controller.server.network.transport.GameUserSession;
import it.unibo.controller.shared.network.packets.UdpHandshakePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class UdpHandshakeHandler implements UdpPacketHandler {
    private static final Logger logger = LoggerFactory.getLogger(UdpHandshakeHandler.class);

    private final CBORMapper cborMapper = new CBORMapper();
    private final GameSessionRegistry sessions;

    public UdpHandshakeHandler(GameSessionRegistry sessions) {
        this.sessions = sessions;
    }

    @Override
    public void handle(InetSocketAddress sender, ByteBufInputStream payload) throws IOException {
        UdpHandshakePacket handshake = cborMapper.readValue((InputStream) payload, UdpHandshakePacket.class);
        GameUserSession session = sessions.get(handshake.username());
        if (session != null) {
            session.setUdpAddress(sender);
            logger.info("Player {} UDP bound to remote endpoint: {}", handshake.username(), sender);
        } else {
            logger.warn("Received UDP handshake for unknown user: {}", handshake.username());
        }
    }
}
