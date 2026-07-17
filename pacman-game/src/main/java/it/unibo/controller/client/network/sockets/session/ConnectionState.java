package it.unibo.controller.client.network.sockets.session;

public enum ConnectionState {
    DISCONNECTED,
    CONNECTING,
    HANDSHAKING,
    CONNECTED,
    FAILED,
    LOST
}