package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response message transmitted during the syn phase by the service.
 * @param publicKey of the sender.
 * @param hash of the key used to check integrity.
 * @param hashType used to create the hash.
 */
public record PublicKeyResponseDTO(
    @JsonProperty("publicKey") String publicKey, 
    @JsonProperty("hash") String hash, 
    @JsonProperty("hashType") String hashType) {

}