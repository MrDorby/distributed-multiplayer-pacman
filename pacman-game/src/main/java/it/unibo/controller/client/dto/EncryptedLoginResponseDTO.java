package it.unibo.controller.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EncryptedLoginResponseDTO(
    @JsonProperty("secretKey") String secretKey, 
    @JsonProperty("encryptedToken") String encryptedToken,
    @JsonProperty("ivParameter") String ivParameter) {
    
}
