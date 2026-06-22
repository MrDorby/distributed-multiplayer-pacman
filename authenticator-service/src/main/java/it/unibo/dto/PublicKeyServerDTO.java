package it.unibo.dto;

/**
 * Response message transmitted by the server during the syn phase.
 */
public record PublicKeyServerDTO(String publicKey, String hash, String hashType) {
    
}
