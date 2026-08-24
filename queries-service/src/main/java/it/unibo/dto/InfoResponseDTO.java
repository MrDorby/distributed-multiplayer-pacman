package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the server to the client.
 * @param response is encrypted.
 */
public record InfoResponseDTO(
    @JsonProperty("response") String response) {
    
}
