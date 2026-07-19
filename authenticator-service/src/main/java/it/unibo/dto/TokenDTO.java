package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Content of the user's login response. It contains the token.
 * @param token
 */

public record TokenDTO(
    @JsonProperty("token") String token) {
    
}
