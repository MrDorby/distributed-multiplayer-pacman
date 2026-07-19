package it.unibo.authentication;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.AuthDetailsService;
import it.unibo.dto.EncryptedTokenDTO;
import it.unibo.dto.LoginDTO;
import it.unibo.dto.TokenDTO;
import it.unibo.dto.PublicKeyClientDTO;
import it.unibo.dto.PublicKeyServerDTO;
import it.unibo.dto.RegisterDTO;
import it.unibo.key.Hash;
import it.unibo.key.KeyManager;
import it.unibo.token.TokenService;
import it.unibo.mongodb.AuthMongoDB;

/**
 * AuthenticatorImpl
 * <p>
 * Service that manages the authentication part.
 */
@RestController
@RequestMapping(value = "/auth")
public class AuthenticatorImpl implements Authenticator {
    
    private final Map<String, PublicKey> users;  // Username and PublicKey
    private final AuthDetailsService authDetailsService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthenticatorImpl(TokenService tokenService, AuthDetailsService authUserDetailsService) {
        this.authDetailsService = authUserDetailsService;
        this.tokenService = tokenService;
        this.users = new HashMap<>();
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        KeyManager.generateRSAKeys();
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
            ResponseEntity<String> responseEntity = ResponseEntity.badRequest().build();
            boolean integrity = Hash.checkHash(publicKeyClientDTO);
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
                String json = mapper.writeValueAsString(new PublicKeyServerDTO(publicKey, hash, publicKeyClientDTO.hashType()));
                responseEntity = ResponseEntity.ok(json);
            }
            return responseEntity;
        } catch (Exception e) {
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
        int ivSize = 16;
        try {
            /* Decrypting the incoming message and authenticating the user. */
            String decryptedLoginDTO = KeyManager.encryptDecryptDataRSA(encryptedLoginDTO, Cipher.DECRYPT_MODE, KeyManager.loadAuthenticatorPrivateKey());
            LoginDTO loginDTO = mapper.readValue(decryptedLoginDTO, LoginDTO.class);
            AuthMongoDB auth = this.authDetailsService.authenticate(loginDTO.username(), loginDTO.password(), bCryptPasswordEncoder);
            
            /* Generating the token and saving the public key of the authenticator. */
            String token = tokenService.generateToken(auth.getUsername());
            String key = Base64.getEncoder().encodeToString(KeyManager.loadAuthenticatorPublicKey().getEncoded());
            this.authDetailsService.addKey(loginDTO.username(), key);
            
            /* Converting the token to JSON format, creating the secret key and encrypting the token. */
            TokenDTO tokenDTO = new TokenDTO(token);
            String jsonToken = mapper.writeValueAsString(tokenDTO);
            SecretKey secretKey = KeyManager.randomSecretKey();
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            String encryptedJsonToken = KeyManager.encryptDecryptDataAES(jsonToken, Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            /* Encrypting the secret key. */
            String secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            String encryptedKey = KeyManager.encryptDecryptDataRSA(secret, Cipher.ENCRYPT_MODE, this.users.get(loginDTO.username()));

            String ivParameters = Base64.getEncoder().encodeToString(ivParameterSpec.getIV());
            String encryptedIV = KeyManager.encryptDecryptDataRSA(ivParameters, Cipher.ENCRYPT_MODE, this.users.get(loginDTO.username()));
            /* Creating the response and converting it to JSON format. */
            EncryptedTokenDTO encryptedTokenDTO = new EncryptedTokenDTO(encryptedKey, encryptedJsonToken, encryptedIV);
            String jsonEncryptedTokenDTO = mapper.writeValueAsString(encryptedTokenDTO);
            return ResponseEntity.ok(jsonEncryptedTokenDTO);
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
            /* Decrypting the incoming message and creating a RegisterDTO. */
            String decryptedRegisterDTO = KeyManager.encryptDecryptDataRSA(encryptedRequest, Cipher.DECRYPT_MODE, KeyManager.loadAuthenticatorPrivateKey());
            RegisterDTO registerDTO = mapper.readValue(decryptedRegisterDTO, RegisterDTO.class);
            
            /* Encrypting the password and creating a user for MongoDB. */
            String encryptedPassword = this.bCryptPasswordEncoder.encode(registerDTO.password());
            AuthMongoDB user = new AuthMongoDB(registerDTO.username(), encryptedPassword, "");

            /* Checking if no user with the given username is on the database. */
            if (this.authDetailsService.loadUserByUsername(user.getUsername()) == null) {
                AuthMongoDB rg = this.authDetailsService.register(user);
                return ResponseEntity.ok("User " + rg.getUsername() + " successfully created!"); 
            }
            return ResponseEntity.internalServerError().body("User already exits!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Defines the procedure to validate the token of a specific user.
     * @param token a TokenDTO Object that represent the token sent via JSON format.
     * @return a ResponseEntity containing the response.
     */
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkToken(@RequestBody TokenDTO token) {
        Instant expireDate = JWT.decode(token.token()).getExpiresAtAsInstant();
        String claim = "username";

        /* Checking on the expiration date and the correctness of the issuer in the token. */
        if (this.tokenService.checkExpirationDate(expireDate) && this.tokenService.checkIssuer(token.token())) {
            /* Obtaining the username from the token and the public key of the authenticator service who signed the token */
            String username = this.tokenService.getClaimFromToken(token.token(), claim);
            String stringKey = this.authDetailsService.loadUserByUsername(username).getKey();
            try {
                /* Verifying the validity of the token and its signature. */
                PublicKey publicKey = KeyManager.getPublicKeyFromString(stringKey);
                DecodedJWT jwt = this.tokenService.getTokenVerified(token.token(), (RSAPublicKey) publicKey);
                return ResponseEntity.ok(jwt.getClaim(claim).asString());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Token expired or Issuer not valid!");
    }
}
