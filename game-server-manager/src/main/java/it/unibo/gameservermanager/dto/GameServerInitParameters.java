package it.unibo.gameservermanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * The initialization parameters of a GameServer.
 * @param matchID the unique ID of the match.
 * @param mapID the unique ID of the game map.
 */
public record GameServerInitParameters(
        @JsonProperty("match-id") String matchID,
        @JsonProperty("map-id") String mapID) {
    public GameServerInitParameters {
        Objects.requireNonNull(matchID);
        Objects.requireNonNull(mapID);
    }
}
