package it.unibo.controller.client.network.sockets.session;

/**
 * Callback system responding to client connection changes.
 */
public interface ClientSessionListener {

    /**
     * Invoked following a session state update.
     */
    void onConnectionStateChanged(ConnectionState state);
}