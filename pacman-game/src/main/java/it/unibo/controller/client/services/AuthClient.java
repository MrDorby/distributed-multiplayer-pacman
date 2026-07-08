package it.unibo.controller.client.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.eclipse.jetty.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.controller.client.dto.EncryptedLoginResponseDTO;
import it.unibo.controller.client.dto.PublicKeyDTO;
import it.unibo.controller.client.dto.RegisterLoginDTO;
import it.unibo.controller.client.key.Hash;

import it.unibo.controller.client.key.KeyManager;

/**
 * 
 * AuthClient
 */
public class AuthClient {
    
    // TODO: Write the docs.
    private static final String SYN_REQUEST = "http://localhost:8080/auth/syn";
    private static final String LOGIN_REQUEST = "http://localhost:8080/auth/login";
    private static final String REGISTER_REQUEST = "http://localhost:8080/auth/register";
    private static final String HASH_TYPE = "SHA-256";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private PublicKey publicKeyAuth;
    private String token;
    private String username;

    public AuthClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        KeyManager.generateRSAKeys();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public void login(String username, String password) throws Exception {
        PublicKeyDTO authPublicKeyDTO = syn();
        this.publicKeyAuth = KeyManager.getPublicKeyFromString(authPublicKeyDTO.publicKey());
        String encryptedString = encryptedRegisterLoginRequest(username, password, publicKeyAuth);
        HttpRequest httpLoginRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(LOGIN_REQUEST))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(encryptedString))
                                    .build();

        HttpResponse<String> loginResponse = httpClient.send(httpLoginRequest, HttpResponse.BodyHandlers.ofString());
        if (loginResponse.statusCode() != HttpStatus.OK_200) {
            throw new Exception(loginResponse.body());
        }
        EncryptedLoginResponseDTO encryptedResponse = objectMapper.readValue(loginResponse.body(), EncryptedLoginResponseDTO.class);
        String secret = KeyManager.encryptDecryptDataRSA(encryptedResponse.secretKey(), Cipher.DECRYPT_MODE, KeyManager.loadAuthenticatorPrivateKey());
        SecretKey secretKey = KeyManager.getSecretKeyFromString(secret);
        String ivParameters = KeyManager.encryptDecryptDataRSA(encryptedResponse.ivParameter(), Cipher.DECRYPT_MODE, KeyManager.loadAuthenticatorPrivateKey());
        String token = KeyManager.encryptDecryptDataAES(encryptedResponse.encryptedToken(), Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivParameters.getBytes()));
        this.token = token;
    }

    /* */
    private String encryptedRegisterLoginRequest(String username, String password, PublicKey key) throws Exception {
        RegisterLoginDTO registerLoginDTO = new RegisterLoginDTO(username, password);
        String registerLogiString = objectMapper.writeValueAsString(registerLoginDTO);
        return KeyManager.encryptDecryptDataRSA(registerLogiString, Cipher.ENCRYPT_MODE, key);
    }


    /**
     * 
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public String register(String username, String password) throws Exception {
        PublicKeyDTO authPublicKeyDTO = syn();
        String encryptedString = encryptedRegisterLoginRequest(username, password, KeyManager.getPublicKeyFromString(authPublicKeyDTO.publicKey()));
        HttpRequest httpRegisterRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(REGISTER_REQUEST))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(encryptedString))
                                    .build();
        
        HttpResponse<String> registerResponse = httpClient.send(httpRegisterRequest, HttpResponse.BodyHandlers.ofString());
        if (registerResponse.statusCode() != HttpStatus.OK_200) {
            throw new Exception(registerResponse.body());
        }
        return registerResponse.body();
    }

    /* */
    private PublicKeyDTO syn() throws Exception {
        PublicKey publicKey = KeyManager.loadAuthenticatorPublicKey();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String hash = Hash.hashing(publicKey.getEncoded(), HASH_TYPE);
        
        PublicKeyDTO publicKeyDTO = new PublicKeyDTO(publicKeyString, hash, HASH_TYPE, username);
        String publicKeyDTOString = objectMapper.writeValueAsString(publicKeyDTO);
        
        HttpRequest httpSynRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(SYN_REQUEST))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(publicKeyDTOString))
                                    .build();
        
        HttpResponse<String> synResponse = httpClient.send(httpSynRequest, HttpResponse.BodyHandlers.ofString());
        if (synResponse.statusCode() != HttpStatus.OK_200) {
            throw new Exception(synResponse.body());
        }

        return objectMapper.readValue(synResponse.body(), PublicKeyDTO.class);
    }

    /**
     * 
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * 
     * @return
     */
    public String getToken() {
        return this.token;
    }
}
