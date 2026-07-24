package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * QuitLobbyRequest
 * @param token
 * @param lobbyId
 */
public record QuitLobbyRequest(
    @JsonProperty("token") String token,
    @JsonProperty("lobby") String lobbyId) {
    
}
