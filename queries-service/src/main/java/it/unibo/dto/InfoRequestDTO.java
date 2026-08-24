package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Containing the encrypted request sent by the client.
 * @param encryptedRequest the request.
 */
public record InfoRequestDTO(
    @JsonProperty("request") String encryptedInfo) {
}
