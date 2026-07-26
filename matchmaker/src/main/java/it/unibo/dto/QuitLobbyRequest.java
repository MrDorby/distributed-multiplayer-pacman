package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * Request of the client to quit from the lobby queue.
 * @param token the token containing the identifier of the client.
 * @param lobbyId the identifier of the lobby.
 */
public record QuitLobbyRequest(
    @JsonProperty("token") String token,
    @JsonProperty("lobby") String lobbyId) {
    
}
