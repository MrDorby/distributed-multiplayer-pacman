package it.unibo.key;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: make a general class for both clients and auth?
/**
 * Handles the generation of the RSA key pair and save on files, if not done yet, the two keys.
 */
public class KeyGenerator {
    
    // TODO: FILE FOR CONSTANTS.
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyPairGenerator.class);
    private static final String PATH = "/resources/keys/";
    private static final String PUBLIC_KEY_FILE = "public_key.der";
    private static final String PRIVATE_KEY_FILE = "private_key.der";
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEYSIZE = 4096; // 2048

    /**
     * Generates the key pair for the RSA algorithm and save them on files.
     */
    public static void generateKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEYSIZE);
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
     * Encrypts and decrypts incoming data by choosing the right mode and passing the key.
     * @param data the content that will be encrypted or decrypted.
     * @param cipherMode Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param key The key that will be used for the chosen mode.
     * @return the String (assumption) of the encrypted/decrypted message.
     */
    public static String encryptDecryptDataWithKey(String data, int cipherMode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            //cipher.init(Cipher.ENCRYPT_MODE, key);
            if (cipherMode != Cipher.ENCRYPT_MODE && cipherMode != Cipher.DECRYPT_MODE) {
                throw new IllegalArgumentException("Cipher mode accepted only: ENCRYPT and DECRYPT!");
            }
            cipher.init(cipherMode, key);
            byte[] dataBytes = data.getBytes();
            return Base64.getEncoder().encodeToString(cipher.doFinal(dataBytes));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    /**
     * @return the Public key of the authentication service.
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static PublicKey loadAuthenticatorPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        try (InputStream inputStream = KeyGenerator.class
                .getClassLoader()
                .getResourceAsStream(PATH + PUBLIC_KEY_FILE)) {
            if (Objects.isNull(inputStream)) {
                throw new IllegalStateException(PUBLIC_KEY_FILE + " not found!");
            }
            X509EncodedKeySpec spec = new X509EncodedKeySpec(inputStream.readAllBytes());
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(spec);
        } /*catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
        } */
    }

    /**
     * @return the Private key of the authentication service.
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static PrivateKey loadAuthenticatorPrivateKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        try (InputStream inputStream = KeyGenerator.class
                .getClassLoader()
                .getResourceAsStream(PATH + PRIVATE_KEY_FILE)) {
            if (Objects.isNull(inputStream)) {
                throw new IllegalStateException(PRIVATE_KEY_FILE + " not found!");
            }
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(inputStream.readAllBytes());
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(spec);
        } /*catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
        } */
    }

    /*
    // PEM extension file.
    toPem("PUBLIC KEY", keyPair.getPublic().getEncoded())
    public static String toPem(String type, byte[] der) {
        String b64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(der);
        return "-----BEGIN " + type + "-----\n" + b64 + "\n-----END " + type + "-----\n";
    }
     */
}


// TODO: do for auth and for client
// PublicKey public_key = CryptographyHelper.ellipticCurveCrypto().getPublic();     
// System.out.println("PUBLIC KEY::" + public_key);

// //converting public key to byte            
// byte[] byte_pubkey = public_key.getEncoded();
// System.out.println("\nBYTE KEY::: " + byte_pubkey);

// //converting byte to String 
// String str_key = Base64.getEncoder().encodeToString(byte_pubkey);
// // String str_key = new String(byte_pubkey,Charset.);
// System.out.println("\nSTRING KEY::" + str_key);

// //converting string to Bytes
// byte_pubkey  = Base64.getDecoder().decode(str_key);
// System.out.println("BYTE KEY::" + byte_pubkey);


// //converting it back to public key
// KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
// public_key = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
// System.out.println("FINAL OUTPUT" + public_key);
