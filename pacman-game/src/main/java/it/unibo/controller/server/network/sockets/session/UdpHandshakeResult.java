package it.unibo.controller.server.network.sockets.session;

public enum UdpHandshakeResult {
    /**
     * The token was valid and the UDP endpoint was successfully bound.
     */
    ACCEPTED,
    /**
     * No session exists for the provided token.
     */
    INVALID_TOKEN,
    /**
     * The token existed but is no longer valid.
     */
    EXPIRED_TOKEN
}
