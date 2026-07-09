package it.unibo.controller.server.network.transport.handler;

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.netty.buffer.ByteBufInputStream;
import it.unibo.controller.server.GameServerNetworkListener;
import it.unibo.controller.server.network.transport.GameSessionRegistry;
import it.unibo.controller.server.network.transport.GameUserSession;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.UdpPacketHandler;
import it.unibo.controller.shared.network.packets.PacmanMovePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class MoveCommandHandler implements UdpPacketHandler {
    private static final Logger logger = LoggerFactory.getLogger(MoveCommandHandler.class);

    private final CBORMapper cborMapper = new CBORMapper();
    private final GameSessionRegistry sessions;
    private final GameServerNetworkListener listener;

    public MoveCommandHandler(GameSessionRegistry sessions, GameServerNetworkListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }

    @Override
    public void handle(InetSocketAddress sender, ByteBufInputStream inputStream) throws IOException {
        PacmanMovePacket packet = cborMapper.readValue((InputStream) inputStream, PacmanMovePacket.class);
        String senderId = packet.pacmanId();
        GameUserSession session = sessions.get(senderId);
        if (session != null && sender.equals(session.getUdpAddress())) {
            listener.onCommandReceived(senderId, new PacmanMoveCommand(packet.pacmanId(), packet.direction()));
        } else {
            logger.warn("Intercepted bad UDP packet from: {}", sender);
        }
    }
}