package it.unibo.controller.server.network.sockets.session;

public enum SessionState {
    /** The TCP socket is open, but we are waiting for the client to complete their UDP token handshake. */
    CONNECTING,
    /** Fully active session. Both TCP and UDP tunnels are ready for gameplay. */
    CONNECTED,
    /** The player was previously active but their TCP connection severed. Session data is safely held in memory. */
    DISCONNECTED,
    /** An existing disconnected session is currently wiring up a brand-new TCP socket, waiting for its UDP pair. */
    RECONNECTING,
    /** The session has been permanently removed. */
    DEAD
}