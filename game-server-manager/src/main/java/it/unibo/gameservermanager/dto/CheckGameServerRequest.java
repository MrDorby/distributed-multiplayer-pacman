package it.unibo.gameservermanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * The request message to check the status of a GameServer.
 * @param matchID the unique ID of the GameServer's match.
 * @param serverName the unique name of the GameServer.
 * @param timeLeft the time left for the GameServer's match.
 */
public record CheckGameServerRequest(
        @JsonProperty("match-id") String matchID,
        @JsonProperty("server-name") String serverName,
        @JsonProperty("time-left") long timeLeft) {
    public CheckGameServerRequest {
        Objects.requireNonNull(serverName);
        Objects.requireNonNull(matchID);
        if(serverName.isEmpty()) {
            throw new IllegalArgumentException("Server name cannot be empty.");
        }
        if (matchID.isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be empty.");
        }
    }
}
