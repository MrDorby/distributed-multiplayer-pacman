package it.unibo.gameservermanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Information about an existing GameServer.
 * @param name the GameServer's unique name.
 * @param IP the GameServer's IP address.
 * @param TCPPort the GameServer's TCP port.
 * @param UDPPort the GameServer's UDP port.
 */
public record GameServerInfo(
        @JsonProperty("name") String name,
        @JsonProperty("ip") String IP,
        @JsonProperty("tcp-port") int TCPPort,
        @JsonProperty("udp-port") int UDPPort) {
    public GameServerInfo {
        Objects.requireNonNull(name);
        Objects.requireNonNull(IP);
    }
}
