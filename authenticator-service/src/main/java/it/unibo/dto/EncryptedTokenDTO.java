package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token encrypted transmitted at the end of the login phase.
 * @param secretKey
 * @param encryptedToken
 * @param ivParameter
 */
public record EncryptedTokenDTO(
    @JsonProperty("secretKey") String secretKey, 
    @JsonProperty("encryptedToken") String encryptedToken,
    @JsonProperty("ivParameter") String ivParameter) {
    
}
