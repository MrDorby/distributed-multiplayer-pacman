package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * DTO containing the response of the matchmaker to the client request.
 * @param typeResponse it can be WAITING or FOUND.
 * @param id if the type is WAITING, the matchmaker will return the lobbyId.
 * Otherwise, if the type is FOUND, the matchmaker will return the matchId.
 */
public record JoinLobbyResponse(
    @JsonProperty("type") LobbyTypeResponse typeResponse,
    @JsonProperty("id") String id) {

}
