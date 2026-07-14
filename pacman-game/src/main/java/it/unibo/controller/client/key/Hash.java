package it.unibo.controller.client.key;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Manages the operations with the hashes.
 */
public class Hash {
    
    /**
     * Checks the integrity of the hash.
     * @param hash
     * @param @hashType
     * @param publicKey
     * @return true if the message has not been compromised, false otherwise.
     * @throws NoSuchAlgorithmException
     */
    public static boolean checkHash(String hash, String hashType, String publicKey) throws NoSuchAlgorithmException {
        String cleanPublicKey = publicKey.trim().replaceAll("\\s+", ""); 
        byte[] keyByte = Base64.getDecoder().decode(cleanPublicKey);
        String diff = hashing(keyByte, hashType);
        return MessageDigest.isEqual(diff.getBytes(StandardCharsets.UTF_8), hash.getBytes());
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
        System.out.println(message.length);
        byte[] hashByte = digest.digest(message);
        String hash = Base64.getEncoder().encodeToString(hashByte);
        return hash;
    }
}
