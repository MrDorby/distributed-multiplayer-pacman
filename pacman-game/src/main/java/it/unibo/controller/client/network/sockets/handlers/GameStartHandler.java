package it.unibo.controller.client.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.client.GameClient;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStartHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameStartHandler.class);
    private final GameClient client;

    public GameStartHandler(GameClient client) {
        this.client = client;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        GameStartPacket gameStartPacket = (GameStartPacket) packet;
        logger.debug("Received {} over TCP from {}", gameStartPacket.getType(), channel.remoteAddress());
        client.onGameStart();
    }
}
