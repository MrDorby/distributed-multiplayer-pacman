package it.unibo.controller.server.network.sockets.handlers;

import it.unibo.controller.server.GameServerController;
import it.unibo.controller.server.network.sockets.GameNetworkServer;
import it.unibo.controller.server.network.sockets.session.GameSessionController;

public record HandlerContext(
        GameSessionController sessionController,
        GameServerController gameServerController,
        GameNetworkServer server
) {}