package it.unibo.dto;

/**
 * Initial message transmitted during the syn phase by the client.
 */
public record PublicKeyClientDTO(String publicKey, String hash, String hashType, String username) {
}