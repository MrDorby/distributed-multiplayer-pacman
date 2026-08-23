package it.unibo.controller.client.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.controller.client.common.PlayerStats;
import it.unibo.controller.client.common.TokenException;
import it.unibo.controller.client.dto.PlayerInfoMongoDB;
import it.unibo.controller.client.dto.PublicKeyRequestDTO;
import it.unibo.controller.client.dto.PublicKeyResponseDTO;
import it.unibo.controller.client.key.Hash;
import it.unibo.controller.client.key.KeyManager;

/**
 * 
 * QueriesClient
 */
public class QueriesClient {
    
    private static final String SYN = "/syn";
    private static final String TOKEN = "/token";
    private static final String INFO = "/info";
    private static final String HASH_TYPE = "SHA-256";

    private final String syn_request;
    private final String token_request;
    private final String info_request;
     
    private final HttpClient httpClient;
    private final KeyManager keyManager;
    private final ObjectMapper objectMapper;

    public QueriesClient(HttpClient httpClient, KeyManager keyManager, UriReader uri) {
        this.httpClient = httpClient;
        this.keyManager = keyManager;
        this.objectMapper = new ObjectMapper();
        this.syn_request = uri.queries() + SYN;
        this.token_request = uri.queries() + TOKEN;
        this.info_request = uri.queries() + INFO;
    }

    /**
     * Method used to get the statistics for the specified player.
     * @param username the player identifier.
     * @param token the Token used to verify the action.
     * @return a new Stat object that contains all the player statistics.
     * @throws Exception
     */
    public PlayerStats getPlayerStats(String username, String token) throws Exception {
        PublicKeyResponseDTO publicKeyResponseDTO = syn(username);
        checkToken(token);
        String encryptedRequest = keyManager.encryptDecryptDataRSA(
                username,
                Cipher.ENCRYPT_MODE, 
                keyManager.getPublicKeyFromString(publicKeyResponseDTO.publicKey()));
        
        HttpRequest httpInfoRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(info_request))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(encryptedRequest))
                                    .build();

        HttpResponse<String> infoResponse = httpClient.send(httpInfoRequest, HttpResponse.BodyHandlers.ofString());
        if (infoResponse.statusCode() != 200) {
            if (infoResponse.statusCode() == 404) {
                return new PlayerStats(username, 0, 0, 0, 0);
            }
            throw new Exception(infoResponse.body());
        }
        String decryptedResponse = keyManager.encryptDecryptDataRSA(infoResponse.body(), Cipher.DECRYPT_MODE, keyManager.loadAuthenticatorPrivateKey());
        PlayerInfoMongoDB playerInfo = objectMapper.readValue(decryptedResponse, PlayerInfoMongoDB.class);
        return new PlayerStats(
            playerInfo.username(), 
            playerInfo.nMatch(), 
            playerInfo.nWins(), 
            (float) playerInfo.nWins() / playerInfo.nMatch(),
            playerInfo.bestScore());
    }

    /* Executes the procedure to check the integrity/validity of the token. */
    private void checkToken(String token) throws Exception {
        //String jsonFormat = objectMapper.writeValueAsString(token);
        HttpRequest httpTokenRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(token_request))
                                    //.header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(token))
                                    .build();

        HttpResponse<String> tokenResponse = httpClient.send(httpTokenRequest, HttpResponse.BodyHandlers.ofString());
        if (tokenResponse.statusCode() != 200) {
            throw new TokenException(tokenResponse.body());
        }
    }

    /* Executes the syn procedure where the two services exchange their public key. */
    private PublicKeyResponseDTO syn(String username) throws Exception {
        PublicKey publicKey = keyManager.loadAuthenticatorPublicKey();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String hash = Hash.hashing(publicKey.getEncoded(), HASH_TYPE);
        PublicKeyRequestDTO publicKeyDTO = new PublicKeyRequestDTO(publicKeyString, hash, HASH_TYPE, username);
        String publicKeyDTOString = objectMapper.writeValueAsString(publicKeyDTO);

        HttpRequest httpSynRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(syn_request))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(publicKeyDTOString))
                                    .build();

        HttpResponse<String> synResponse = httpClient.send(httpSynRequest, HttpResponse.BodyHandlers.ofString());
        if (synResponse.statusCode() != 200) {
            throw new Exception(synResponse.body());
        }
        return objectMapper.readValue(synResponse.body(), PublicKeyResponseDTO.class);
    }
}
