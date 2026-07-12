package it.unibo.controller.server;

import it.unibo.controller.server.engine.GameEngineListener;
import it.unibo.controller.server.network.sockets.session.SessionLifecycleListener;

public interface GameServer extends GameServerNetworkListener,
        GameEngineListener, SessionLifecycleListener {

    void start() throws Exception;

    void stop() throws Exception;
}
