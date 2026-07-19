package it.unibo.controller.server;

import it.unibo.controller.shared.engine.RemoteGameEngineListener;
import it.unibo.controller.server.network.sockets.session.GameSessionLifecycleListener;

public interface GameServer extends GameServerNetworkListener,
        RemoteGameEngineListener, GameSessionLifecycleListener {

    void start() throws Exception;

    void stop() throws Exception;
}
