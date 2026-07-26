package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * DTO representing the request to join a lobby
 * @param token provided by the user.
 * @param map the name of the map that defines the queue.
 */
public record JoinLobbyRequest(
    @JsonProperty("token") String token,
    @JsonProperty("map") String map) {
    
}
