package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines the message containing the token of the user.
 * @param token represented as a String.
 */

public record TokenDTO(
    @JsonProperty("token") String token) {
    
}
