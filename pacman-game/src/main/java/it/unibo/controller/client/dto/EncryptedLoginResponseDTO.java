package it.unibo.controller.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the encrypted response received during the login phase.
 * @param secretKey the encrypted secret key.
 * @param encryptedToken the encrypted token for the user.
 * @param ivParameter the parameter necessary for the AES algorithm.
 */
public record EncryptedLoginResponseDTO(
    @JsonProperty("secretKey") String secretKey, 
    @JsonProperty("encryptedToken") String encryptedToken,
    @JsonProperty("ivParameter") String ivParameter) {
    
}
