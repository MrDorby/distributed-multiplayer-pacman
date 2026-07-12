package it.unibo.controller.server.network.sockets.handlers;

import it.unibo.controller.server.GameServer;
import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSessionController;

public record HandlerContext(
        GameSessionController sessions,
        GameServer server,
        GameServerGateway gateway
) {}