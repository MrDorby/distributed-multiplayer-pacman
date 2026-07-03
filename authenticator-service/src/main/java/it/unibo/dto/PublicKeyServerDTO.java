package it.unibo.dto;

/**
 * Response message transmitted by the server during the syn phase.
 * @param publicKey
 * @param hash
 * @param hashType
 */
public record PublicKeyServerDTO(String publicKey, String hash, String hashType) {
    
}
