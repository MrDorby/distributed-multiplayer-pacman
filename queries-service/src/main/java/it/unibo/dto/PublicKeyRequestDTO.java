package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Initial message transmitted during the syn phase by the client.
 * @param publicKey of the sender.
 * @param hash of the key used to check integrity.
 * @param hashType used to create the hash.
 * @param username of the 
 */
public record PublicKeyRequestDTO(  
    @JsonProperty("publicKey") String publicKey, 
    @JsonProperty("hash") String hash, 
    @JsonProperty("hashType") String hashType, 
    @JsonProperty("username") String username) {

}