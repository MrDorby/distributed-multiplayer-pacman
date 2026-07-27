package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.unibo.mongodb.Socket;

/**
 * Message transmitted between the Matchmaker and the GameServer Manager.
 * Also used as response for the GameServer requet. 
 * @param matchId the identifier of the match.
 * @param gameServer the information needed to connect with the GameServer.
 */
public record GameServerResponse(
    @JsonProperty("matchId") String matchId,
    @JsonProperty("socket") Socket gameServer
) {
    
}
