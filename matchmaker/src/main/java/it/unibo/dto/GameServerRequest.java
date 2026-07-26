package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request sent by the client to receive the information about 
 * the GameServer it wants to connect with. 
 * @param token of the player.
 * @param matchId the identifier of the match.
 */
public record GameServerRequest(
    @JsonProperty("token") String token,
    @JsonProperty("match") String matchId) {
    
}
