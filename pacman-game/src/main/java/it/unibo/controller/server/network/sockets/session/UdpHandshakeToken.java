package it.unibo.controller.server.network.sockets.session;

import java.time.Instant;

public record UdpHandshakeToken(String value, Instant expiresAt) {
    public boolean expired() {
        return Instant.now().isAfter(expiresAt);
    }
}
