package it.unibo.controller.client.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.client.GameClient;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.GameEndPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameEndHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameEndHandler.class);
    private final GameClient client;

    public GameEndHandler(GameClient client) {
        this.client = client;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        GameEndPacket gameEndPacket = (GameEndPacket) packet;
        logger.debug("Received {} over TCP from {}", gameEndPacket.getType(), channel.remoteAddress());
        client.onGameEnd();
    }
}
