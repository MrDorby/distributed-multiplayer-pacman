package it.unibo.key;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.dto.PublicKeyClientDTO;

public class Hash {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyPairGenerator.class);

    /**
     * Checks the integrity of the hash.
     * @param publicKeyClientDTO
     * @return true if the message has not been compromised, false otherwise.
     * @throws NoSuchAlgorithmException
     */
    public static boolean checkHash(PublicKeyClientDTO publicKeyClientDTO) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(publicKeyClientDTO.hashType());
        byte[] hashByte = digest.digest(publicKeyClientDTO.publicKey().getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashByte).equals(publicKeyClientDTO.hash());
        //return MessageDigest.isEqual(hashByte, publicKeyClientDTO.hash().getBytes());
    }

    /**
     * Applies the hashing mechanism on the message.
     * @param message as a byte[].
     * @param hashType the type of hashing to apply.
     * @return the string of the hash produced.
     */
    public static String hashing(byte[] message, String hashType) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(hashType);
            byte[] hashByte = digest.digest(message);
            hash = Base64.getEncoder().encodeToString(hashByte);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
        }
        return hash;
    }
}
