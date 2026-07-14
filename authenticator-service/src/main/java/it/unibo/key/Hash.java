package it.unibo.key;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import it.unibo.dto.PublicKeyClientDTO;

public class Hash {
    
    /**
     * Checks the integrity of the hash.
     * @param publicKeyClientDTO
     * @return true if the message has not been compromised, false otherwise.
     * @throws NoSuchAlgorithmException
     */
    public static boolean checkHash(PublicKeyClientDTO publicKeyClientDTO) throws NoSuchAlgorithmException {
        String cleanPublicKey = publicKeyClientDTO.publicKey().trim().replaceAll("\\s+", "");
        byte[] keyByte = Base64.getDecoder().decode(cleanPublicKey);
        String diff = hashing(keyByte, publicKeyClientDTO.hashType());
        return MessageDigest.isEqual(diff.getBytes(StandardCharsets.UTF_8), publicKeyClientDTO.hash().getBytes());
    }

    /**
     * Applies the hashing mechanism on the message.
     * @param message as a byte[].
     * @param hashType the type of hashing to apply.
     * @return the string of the hash produced.
     * @throws NoSuchAlgorithmException 
     */
    public static String hashing(byte[] message, String hashType) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(hashType);
        byte[] hashByte = digest.digest(message);
        String hash = Base64.getEncoder().encodeToString(hashByte);
        return hash;
    }
}
