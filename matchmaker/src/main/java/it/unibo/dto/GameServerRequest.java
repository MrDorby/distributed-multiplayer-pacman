package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * GameServerRequest
 * @param token
 * @param lobbyId
 */
public record GameServerRequest(
    @JsonProperty("token") String token,
    @JsonProperty("lobby") String lobbyId) {
    
}
