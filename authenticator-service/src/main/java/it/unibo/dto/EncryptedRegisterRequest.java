package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * EncryptedRegisterRequest
 * @param encryptedRequest
 */
public record EncryptedRegisterRequest(
    @JsonProperty("register") String encryptedRequest) {
}
