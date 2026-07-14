package it.unibo.controller.client.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.client.GameClient;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.handlers.UdpHandler;
import it.unibo.controller.shared.network.sockets.packets.GameContextPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.translation.GameContextDecoder;
import it.unibo.controller.shared.network.translation.GameContextDecoderImpl;
import it.unibo.model.entities.SpeculativeEntityFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class GameContextHandler implements TcpHandler, UdpHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameContextHandler.class);
    private final GameContextDecoder decoder = new GameContextDecoderImpl(new SpeculativeEntityFactoryImpl());
    private final GameClient client;

    public GameContextHandler(GameClient client) {
        this.client = client;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        GameContextPacket gameContextPacket = (GameContextPacket) packet;
        GameContextDTO contextDTO = gameContextPacket.context();
        logger.debug("Received {} over TCP from {}", gameContextPacket.getType(), channel.remoteAddress());
        client.onGameContext(decoder.decode(contextDTO));
    }

    @Override
    public void handle(InetSocketAddress sender, NetworkPacket packet) {
        GameContextPacket gameContextPacket = (GameContextPacket) packet;
        GameContextDTO contextDTO = gameContextPacket.context();
        logger.trace("Received {} over UDP from {}", gameContextPacket.getType(), sender);
        client.onGameContext(decoder.decode(contextDTO));
    }
}
