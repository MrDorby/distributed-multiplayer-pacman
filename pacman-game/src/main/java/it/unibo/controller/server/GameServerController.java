package it.unibo.controller.server;

import it.unibo.controller.server.engine.GameLifecycleListener;
import it.unibo.controller.server.network.sockets.session.SessionLifecycleListener;

public interface GameServerController extends GameServerNetworkListener,
        GameContextBroadcaster, GameLifecycleListener, SessionLifecycleListener {

    void start() throws Exception;

    void stop() throws Exception;
}
