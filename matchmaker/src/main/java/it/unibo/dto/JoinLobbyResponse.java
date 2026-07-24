package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * JoinLobbyResponse
 * @param typeResponse
 * @param lobbyId
 */
public record JoinLobbyResponse(
    @JsonProperty("type") LobbyTypeResponse typeResponse,
    @JsonProperty("lobby") String lobbyId) {

}
