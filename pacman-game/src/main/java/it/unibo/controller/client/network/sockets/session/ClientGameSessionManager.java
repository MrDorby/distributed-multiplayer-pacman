package it.unibo.controller.client.network.sockets.session;

/**
 * Coordinates networking events, connection states, and retry tasks
 * associated with a single client session.
 */
public interface ClientGameSessionManager {

    /**
     * Registers a listener to be alerted on lifecycle state modifications.
     */
    void addListener(ClientSessionListener listener);

    /**
     * Gets the username string associated with this session.
     */
    String getUsername();

    /**
     * Begins initial connection sequence over reliable TCP transport.
     */
    void joinServer();

    /**
     * Invoked upon TCP response containing security token for UDP handshaking.
     */
    void onJoinAck(String token);

    /**
     * Invoked once UDP communication validation successfully finishes.
     */
    void onUdpReady();

    /**
     * Invoked when the connection with the server is cut abruptly.
     */
    void onConnectionLost();

    /**
     * Gracefully signals disconnection, closing active background processes.
     */
    void disconnect();

    /**
     * Tears down background pools and cancels pending operations.
     */
    void close();
}
