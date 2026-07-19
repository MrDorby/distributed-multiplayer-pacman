package it.unibo.controller.client.network.sockets.handlers;

import io.netty.channel.Channel;
import it.unibo.controller.client.network.sockets.session.ClientGameSessionManager;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpReadyHandler implements TcpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UdpReadyHandler.class);

    private final ClientGameSessionManager sessionController;

    public UdpReadyHandler(ClientGameSessionManager sessionController) {
        this.sessionController = sessionController;
    }

    @Override
    public void handle(Channel channel, NetworkPacket packet) {
        sessionController.onUdpReady();
    }
}