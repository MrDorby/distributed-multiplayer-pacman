package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response message transmitted by the server during the syn phase.
 * @param publicKey
 * @param hash
 * @param hashType
 */
public record PublicKeyServerDTO(
    @JsonProperty("publicKey") String publicKey, 
    @JsonProperty("hash") String hash, 
    @JsonProperty("hashType") String hashType) {
    
}
