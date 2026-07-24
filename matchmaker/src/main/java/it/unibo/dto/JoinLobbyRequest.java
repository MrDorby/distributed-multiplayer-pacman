package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * JoinLobbyRequest
 * @param token
 * @param map
 */
public record JoinLobbyRequest(
    @JsonProperty("token") String token,
    @JsonProperty("map") String map) {
    
}
