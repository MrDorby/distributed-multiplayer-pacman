package it.unibo.controller.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Initial message transmitted during the syn phase by the client.
 * @param publicKey
 * @param hash
 * @param hashType
 * @param username
 */
public record PublicKeyDTO(  
    @JsonProperty("publicKey") String publicKey, 
    @JsonProperty("hash") String hash, 
    @JsonProperty("hashType") String hashType, 
    @JsonProperty("username") String username) {

}
