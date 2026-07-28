package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to remove a player from a specific match.
 * @param token the string containing the token.
 * @param matchId the identifier of the match.
 */
public record RemoveRequest(
    @JsonProperty("token") String token,
    @JsonProperty("match") String matchId) {
    
}
