package it.unibo.controller.shared.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.client.GameClientController;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStartHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameStartHandler.class);
    private final GameClientController controller;

    public GameStartHandler(GameClientController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        GameStartPacket gameStartPacket = (GameStartPacket) packet;
        logger.debug("Received {} over TCP from {}", PacketType.GAME_START, channel.remoteAddress());
        controller.onGameStart();
    }
}
