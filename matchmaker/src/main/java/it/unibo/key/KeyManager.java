package it.unibo.key;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the generation of the RSA key pair and save on files, if not done yet, the two keys.
 * Also performs AES encryption and decryption operations.
 */
public class KeyManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyPairGenerator.class);
    private static final String PATH = "keys";
    private static final String PUBLIC_KEY_FILE = "public_key.der";
    private static final String PRIVATE_KEY_FILE = "private_key.der";

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private static final String RSA_INSTANCE = "RSA/ECB/PKCS1Padding";
    private static final String AES_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final int RSA_KEYSIZE = 4096; // 2048
    private static final int AES_KEYSIZE = 256;

    /**
     * Generates the key pair for the RSA algorithm and save them on files.
     */
    public static void generateRSAKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(RSA_KEYSIZE);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            Path keyDir = Paths.get(PATH);
            if (!Files.exists(keyDir)) {
                Files.createDirectory(keyDir);
            }
            Path publicKey = Paths.get(PATH, PUBLIC_KEY_FILE);
            Files.write(publicKey, keyPair.getPublic().getEncoded());
            Path privateKey = Paths.get(PATH, PRIVATE_KEY_FILE);
            Files.write(privateKey, keyPair.getPrivate().getEncoded());

        } catch (NoSuchAlgorithmException | IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Encrypts and decrypts with RSA the incoming data by choosing the right mode and passing the key.
     * @param data : the content that will be encrypted or decrypted.
     * @param cipherMode : Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param key : The key that will be used for the chosen mode (AES or RSA).
     * @return the String of the encrypted/decrypted message.
     * @throws Exception
     */
    public static String encryptDecryptDataRSA(String data, int cipherMode, Key key) throws Exception {
        if (cipherMode != Cipher.ENCRYPT_MODE && cipherMode != Cipher.DECRYPT_MODE) {
            throw new IllegalArgumentException("Cipher mode accepted only: ENCRYPT and DECRYPT!");
        }
        Cipher cipher = Cipher.getInstance(RSA_INSTANCE);
            cipher.init(cipherMode, key);
        return encryptDecrypt(data, cipherMode, cipher);
    }

    /**
     * Encrypts and decrypts with AES the incoming data by choosing the right mode and passing the key.
     * @param data : the content that will be encrypted or decrypted.
     * @param cipherMode : Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param key : the key that will be used for the chosen mode (AES or RSA).
     * @param ivParameterSpec : the initialization vector for the algorithm.
     * @return the String of the encrypted/decrypted message.
     * @throws Exception
     */
    public static String encryptDecryptDataAES(String data, int cipherMode, SecretKey key, IvParameterSpec ivParameterSpec) throws Exception {
        if (cipherMode != Cipher.ENCRYPT_MODE && cipherMode != Cipher.DECRYPT_MODE) {
            throw new IllegalArgumentException("Cipher mode accepted only: ENCRYPT and DECRYPT!");
        }
        Cipher cipher = Cipher.getInstance(AES_INSTANCE);
        cipher.init(cipherMode, key, ivParameterSpec);
        return encryptDecrypt(data, cipherMode, cipher);
    }
        

    /**
     * @return the Public key of the authentication service.
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static PublicKey loadAuthenticatorPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Path path = Path.of(PATH, PUBLIC_KEY_FILE);
        try (InputStream inputStream = Files.newInputStream(path)) {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(inputStream.readAllBytes());
            return KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(spec);
        }
    }

    /**
     * @return the Private key of the authentication service.
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static PrivateKey loadAuthenticatorPrivateKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Path path = Path.of(PATH, PRIVATE_KEY_FILE);
        try (InputStream inputStream = Files.newInputStream(path)) {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(inputStream.readAllBytes());
            return KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(spec);
        }
    }

    //toPem("PUBLIC KEY", keyPair.getPublic().getEncoded())
    /**
     * Mapping the key for RSA in the Pem format/extension.
     * @param type "PUBLIC KEY" or "PRIVATE KEY".
     * @param der the byte[] read from the .der file.
     * @return a String version of the key in Pem format.
     */
    public static String toPem(String type, byte[] der) {
        String b64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(der);
        return "-----BEGIN " + type + "-----\n" + b64 + "\n-----END " + type + "-----\n";
    }

    /**
     * Generates the secret key for the AES alghoritm.
     * @return the new Random Secret Key.
     * @throws Exception
     */
    public static SecretKey randomSecretKey() throws Exception {
        javax.crypto.KeyGenerator keyGenerator = javax.crypto.KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEYSIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }

    /**
     * Converts the input String in a Public Key object.
     * @param key the input String.
     * @return the related Public Key.
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromString(String key) throws Exception {
        byte[] keyByte = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyByte);
        return KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(spec);
    }

    /**
     * Converts the input String in a Private Key object.
     * @param key the input String.
     * @return the related Private Key.
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromString(String key) throws Exception {
        byte[] keyByte = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyByte);
        return KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(spec);
    }

    /**
     * Convertion from String to SecretKey for AES algorithm.
     * @param key the String version of the original secret key.
     * @return the SecretKey.
     */
    public static SecretKey getSecretKeyFromString(String key) {
        byte[] keyByte = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(keyByte, AES_ALGORITHM);
        return secretKey;
    }

    /* Encrypts and decrypts data based on the key received in input. */
    private static String encryptDecrypt(String data, int cipherMode, Cipher cipher) throws Exception {
        if (cipherMode == Cipher.DECRYPT_MODE) {
            String cleanedData = data.trim().replaceAll("\\s+", ""); 
            byte[] encryptedDataBytes = Base64.getMimeDecoder().decode(cleanedData);
            byte[] output = cipher.doFinal(encryptedDataBytes);
            return new String(output, StandardCharsets.UTF_8);
        } else {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] output = cipher.doFinal(dataBytes);
            return Base64.getEncoder().encodeToString(output); 
        }
    }
}