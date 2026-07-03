package it.unibo.authentication;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.AuthDetailsService;
import it.unibo.dto.EncryptedRegisterRequest;
import it.unibo.dto.LoginDTO;
import it.unibo.dto.LoginResponse;
import it.unibo.dto.PublicKeyClientDTO;
import it.unibo.dto.PublicKeyServerDTO;
import it.unibo.dto.RegisterDTO;
import it.unibo.key.Hash;
import it.unibo.key.KeyGenerator;
import it.unibo.token.TokenService;
import it.unibo.mongodb.AuthMongoDB;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticatorImpl implements Authenticator {
    
    private final Map<String, PublicKey> users;  // Username/UserID and PublicKey
    private final AuthDetailsService authDetailsService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthenticatorImpl(TokenService tokenService, AuthDetailsService authUserDetailsService) {
        this.authDetailsService = authUserDetailsService;
        this.tokenService = tokenService;
        this.users = new HashMap<>();
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        KeyGenerator.generateKeys();
    }

    /**
     * Defines the communication between the client and the authenticator for the synchronization phase.
     * @param publicKeyClientDTO the initial input received by the client.
     * @return ResponseEntity<String> in json and containing the PublicKeyServerDTO. 
     * In case of problem, the output will be a response empty with a HttpStatus.
     */
    @PostMapping(value = "/syn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> syn(@RequestBody PublicKeyClientDTO publicKeyClientDTO) {
        try {
            System.out.println(publicKeyClientDTO);
            ResponseEntity<String> responseEntity = ResponseEntity.badRequest().build();
            boolean integrity = Hash.checkHash(publicKeyClientDTO);
            if (integrity) {
                PublicKey authPub = KeyGenerator.loadAuthenticatorPublicKey();
                byte[] authPubByte = authPub.getEncoded();
                String hash = Hash.hashing(authPubByte, publicKeyClientDTO.hashType());
                String publicKey = Base64.getEncoder().encodeToString(authPubByte);
                this.users.put(publicKeyClientDTO.username(), authPub);
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(new PublicKeyServerDTO(publicKey, hash, publicKeyClientDTO.hashType()));
                responseEntity = ResponseEntity.ok(json);
            }
            return responseEntity;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            if (e instanceof NoSuchAlgorithmException) {
                // In this case, the auth send a empty response to indicate that something wrong happened and the 
                // user needs to re-authenticate itself.
                //return new ResponseEntity<String>("", HttpStatus.BAD_REQUEST);
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            // In this case, the server had a problem.
            //return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Defines the procedure of authentication for the client to the server, checking the credentials sent. 
     * @param loginRequest The body of the message containing username and password encrypted.
     * @return ResponseEntity<String> in json containing the LoginResponse serialized.
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody String encryptedLoginDTO) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String decryptedLoginDTO = KeyGenerator.encryptDecryptDataWithKey(encryptedLoginDTO, Cipher.DECRYPT_MODE, KeyGenerator.loadAuthenticatorPrivateKey());
            LoginDTO loginDTO = mapper.readValue(decryptedLoginDTO, LoginDTO.class);
            //String encryptedPassword = this.bCryptPasswordEncoder.encode(loginDTO.password());
            AuthMongoDB auth = this.authDetailsService.authenticate(loginDTO.username(), loginDTO.password(), bCryptPasswordEncoder);
            String token = tokenService.generateToken(auth.getUsername());
            String key = Base64.getEncoder().encodeToString(KeyGenerator.loadAuthenticatorPublicKey().getEncoded());
            this.authDetailsService.addKey(loginDTO.username(), key);
            LoginResponse loginResponse = new LoginResponse(token);
            String json = mapper.writeValueAsString(loginResponse);
            System.out.println("\n\n\nL " + json.length());
            String encryptedJson = KeyGenerator.encryptDecryptDataWithKey(json, Cipher.ENCRYPT_MODE, this.users.get(loginDTO.username()));
            return ResponseEntity.ok(encryptedJson);
        } catch (Exception e) {
            //return new ResponseEntity<String>("", HttpStatus.CONFLICT);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Defines the procedure of registration for the client to the server, adding credential to the database. 
     * @param registerRequest The body of the message containing username and password encrypted.
     * @return ResponseEntity in json containing the Response.
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody String encryptedRequest) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // EncryptedRegisterRequest registerRequest = mapper.readValue(encryptedRequest, EncryptedRegisterRequest.class);
            //System.out.println(registerRequest.encryptedRequest());
            String decryptedRegisterDTO = KeyGenerator.encryptDecryptDataWithKey(encryptedRequest, Cipher.DECRYPT_MODE, KeyGenerator.loadAuthenticatorPrivateKey());
            RegisterDTO registerDTO = mapper.readValue(decryptedRegisterDTO, RegisterDTO.class);
            String encryptedPassword = this.bCryptPasswordEncoder.encode(registerDTO.password());
            AuthMongoDB user = new AuthMongoDB(registerDTO.username(), encryptedPassword, "");
            if (this.authDetailsService.loadUserByUsername(user.getUsername()) == null) {
                AuthMongoDB rg = this.authDetailsService.register(user);
                return ResponseEntity.ok("User " + rg.getUsername() + " successfully created!"); 
            }
            return ResponseEntity.internalServerError().body("User already exits!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // TODO: add the part with the match maker
}
