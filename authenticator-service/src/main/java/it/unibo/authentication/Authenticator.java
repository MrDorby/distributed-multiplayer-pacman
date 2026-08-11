package it.unibo.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import it.unibo.dto.PublicKeyClientDTO;
import it.unibo.dto.TokenDTO;

/**
 * Concept of the service needs to authenticate the users.
 */
public interface Authenticator {
    
    /**
     * Defines the communication between the client and the authenticator for the synchronization phase.
     * @param publicKeyClientDTO the initial input received by the client.
     * @return ResponseEntity<String> in json and containing the PublicKeyServerDTO. 
     * In case of problem, the output will be a response empty with a HttpStatus.
     */
    ResponseEntity<String> syn(@RequestBody PublicKeyClientDTO publicKeyClientDTO);

    /**
     * Defines the procedure of authentication for the client to the server, checking the credentials sent. 
     * @param loginRequest The body of the message containing username and password encrypted.
     * @return ResponseEntity<String> in json containing the LoginResponse serialized.
     */
    ResponseEntity<String> login(@RequestBody String encryptedLoginDTO);

    /**
     * Defines the procedure of registration for the client to the server, adding credential to the database. 
     * @param registerRequest The body of the message containing username and password encrypted.
     * @return ResponseEntity in json containing the Response.
     */
    ResponseEntity<String> register(@RequestBody String registerRequest);

    /**
     * Defines the procedure to validate the token of a specific user.
     * @param token a DTO containing the token.
     * @return a ResponseEntity containing the response.
     */
    ResponseEntity<String> checkToken(@RequestBody TokenDTO token);

    /**
     * Defines the procedure to extract the public key of the user.
     * @param username the identifier for the player.
     * @return a ResponseEntity containing the response.
     */
    ResponseEntity<String> getPublicKeyOfUser(@RequestBody String username);
}
