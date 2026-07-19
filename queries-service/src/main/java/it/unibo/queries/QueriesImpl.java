package it.unibo.queries;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.QueriesDetailsService;
import it.unibo.dto.PublicKeyRequestDTO;
import it.unibo.dto.PublicKeyResponseDTO;
import it.unibo.key.Hash;
import it.unibo.key.KeyManager;
import it.unibo.mongodb.PlayerInfoMongoDB;

/**
 * QueriesImpl
 * <p>
 * Service that manages the requests to get players informations.
 */
@RestController
@RequestMapping(value = "/queries")
public class QueriesImpl implements Queries {

    // TODO: Modify the HTTP REQUEST
    private static final String AUTHENTICATOR_REQUEST = "http://localhost:8080/auth/token";
    private final Map<String, PublicKey> users;  // Username and PublicKey
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final QueriesDetailsService queriesDetailsService;

    public QueriesImpl(QueriesDetailsService queriesDetailsService) {
        this.queriesDetailsService = queriesDetailsService;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.users = new HashMap<>();
        KeyManager.generateRSAKeys();
    }

    // TODO: check https://spring.io/guides/gs/consuming-rest
    @Override
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkTokenPermission(@RequestBody String token) { 
        HttpRequest httpTokenRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(AUTHENTICATOR_REQUEST))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(token))
                                    .build();
        
        try {
            HttpResponse<String> tokenResponse = httpClient.send(httpTokenRequest, HttpResponse.BodyHandlers.ofString());
            if (tokenResponse.statusCode() == HttpStatus.OK.value()) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body(tokenResponse.body());
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // TODO: Make the send async
    @Override
    @PostMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPlayerInfo(@RequestBody String encrytpedRequest) {
        try {
            /* Decrypting the incoming message and authenticating the user. */
            String username = KeyManager.encryptDecryptDataRSA(encrytpedRequest, Cipher.DECRYPT_MODE, KeyManager.loadAuthenticatorPrivateKey());
            
            PlayerInfoMongoDB player = this.queriesDetailsService.loadUserByUsername(username);
            if (Objects.isNull(player)) {
                return ResponseEntity.notFound().build();
            }
            String playerString = this.objectMapper.writeValueAsString(player);
            String encryptedResponse = KeyManager.encryptDecryptDataRSA(playerString, Cipher.ENCRYPT_MODE, this.users.get(username));
            return ResponseEntity.ok(encryptedResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Override
    @PostMapping(value = "/syn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> syn(@RequestBody PublicKeyRequestDTO publicKeyClientDTO) {
        try {
            ResponseEntity<String> responseEntity = ResponseEntity.badRequest().build();
            boolean integrity = Hash.checkHash(publicKeyClientDTO.hash(), publicKeyClientDTO.hashType(), publicKeyClientDTO.publicKey());
            /* Checking the integrity of the message. */
            if (integrity) {
                /* Creating the hash of the Public Key. */
                PublicKey authPub = KeyManager.loadAuthenticatorPublicKey();
                byte[] authPubByte = authPub.getEncoded();
                String hash = Hash.hashing(authPubByte, publicKeyClientDTO.hashType());
                
                /* Storaging the Public Key of the user. */
                String publicKey = Base64.getEncoder().encodeToString(authPubByte);
                this.users.put(publicKeyClientDTO.username(), KeyManager.getPublicKeyFromString(publicKeyClientDTO.publicKey()));
                
                /* Mapping the Message to a String in JSON format. */
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(new PublicKeyResponseDTO(publicKey, hash, publicKeyClientDTO.hashType()));
                responseEntity = ResponseEntity.ok(json);
            }
            return responseEntity;
        } catch (Exception e) {
            if (e instanceof NoSuchAlgorithmException) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}