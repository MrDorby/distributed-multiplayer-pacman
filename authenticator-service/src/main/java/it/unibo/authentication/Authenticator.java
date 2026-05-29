package it.unibo.authentication;

import org.springframework.http.ResponseEntity;

import it.unibo.dto.LoginDTO;
import it.unibo.dto.LoginResponse;

/**
 * Concept of the service needs to authenticate the users.
 */
public interface Authenticator {
    
    ResponseEntity<LoginResponse> login(LoginDTO loginRequest);

    ResponseEntity<Void> register();

}
