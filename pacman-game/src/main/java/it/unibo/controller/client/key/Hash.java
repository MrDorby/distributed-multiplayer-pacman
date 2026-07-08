package it.unibo.controller.client.key;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


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
        MessageDigest digest = MessageDigest.getInstance(hashType);
        byte[] hashByte = digest.digest(publicKey.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashByte).equals(hash);
        //return MessageDigest.isEqual(hashByte, publicKeyClientDTO.hash().getBytes());
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
