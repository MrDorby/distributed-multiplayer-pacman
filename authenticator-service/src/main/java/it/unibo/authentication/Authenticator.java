package it.unibo.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import it.unibo.dto.PublicKeyClientDTO;
import it.unibo.dto.TokenDTO;

/**
 * Concept of the service needs to authenticate the users.
 */
public interface Authenticator {
    
    //TODO: Insert here the docs instead in AuthImpl
    ResponseEntity<String> syn(@RequestBody PublicKeyClientDTO publicKeyClientDTO);

    ResponseEntity<String> login(@RequestBody String encryptedLoginDTO);

    ResponseEntity<String> register(@RequestBody String registerRequest);

    ResponseEntity<String> checkToken(@RequestBody TokenDTO token);
}
