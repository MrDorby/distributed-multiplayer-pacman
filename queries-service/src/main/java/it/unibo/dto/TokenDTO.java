package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @param token
 */

public record TokenDTO(
    @JsonProperty("token") String token) {
    
}
