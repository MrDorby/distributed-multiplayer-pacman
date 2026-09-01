package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message received to remove a specific match from the db.
 * @param matchId  the identifier of the match to be removed.
 */
public record DeleteMatchDTO(
    @JsonProperty("match-id") String matchId) {
    
}
