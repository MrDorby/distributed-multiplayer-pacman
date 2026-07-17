package it.unibo.controller.server.network.sockets.session;

/**
 * Receives notifications about player session lifecycle changes.
 *
 * <p>Listeners are notified by the thread that processes the corresponding
 * network event.</p>
 */
public interface GameSessionLifecycleListener {

    /**
     * Called when a new player completes the connection process.
     *
     * <p>The session has transitioned from {@link GameSessionState#CONNECTING}
     * to {@link GameSessionState#CONNECTED}. TCP and UDP communication are
     * available when this callback is invoked.</p>
     *
     * @param session the newly connected player's session
     */
    void onPlayerConnected(GameSession session);

    /**
     * Called when a previously disconnected player successfully reconnects.
     *
     * <p>The session has transitioned from
     * {@link GameSessionState#RECONNECTING} to {@link GameSessionState#CONNECTED}.
     * The session object is reused, but its TCP channel and UDP binding may
     * have changed.</p>
     *
     * @param session the reconnected player's session
     */
    void onPlayerReconnected(GameSession session);

    /**
     * Called when a player's connection is lost.
     *
     * <p>The session has transitioned to {@link GameSessionState#DISCONNECTED}.
     * Session data may remain available for recovery until the session is permanently removed.</p>
     *
     * @param session the disconnected player's session
     */
    void onPlayerDisconnected(GameSession session);
}
