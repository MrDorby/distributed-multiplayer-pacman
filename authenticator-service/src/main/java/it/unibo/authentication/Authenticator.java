package it.unibo.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import it.unibo.dto.PublicKeyClientDTO;

/**
 * Concept of the service needs to authenticate the users.
 */
public interface Authenticator {
    
    ResponseEntity<String> syn(@RequestBody PublicKeyClientDTO publicKeyClientDTO);

    ResponseEntity<String> login(@RequestBody String encryptedLoginDTO);

    ResponseEntity<String> register(@RequestBody String registerRequest);

}
