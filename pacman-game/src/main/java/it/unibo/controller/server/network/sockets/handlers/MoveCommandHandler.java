package it.unibo.controller.server.network.sockets.handlers;

import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.sockets.handlers.UdpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacmanMovePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class MoveCommandHandler implements UdpHandler {
    private static final Logger logger = LoggerFactory.getLogger(MoveCommandHandler.class);
    private final HandlerContext ctx;

    public MoveCommandHandler(HandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(InetSocketAddress sender, NetworkPacket packet) {
        PacmanMovePacket movePacket = (PacmanMovePacket) packet;
        logger.debug("Received {} over UDP from {}", movePacket.getType(), sender);
        ctx.server().onCommandReceived(new PacmanMoveCommand(movePacket.pacmanId(), movePacket.direction()));
    }
}