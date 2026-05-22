package it.unibo.authentication;

import org.springframework.http.ResponseEntity;

import it.unibo.dto.LoginRequest;
import it.unibo.dto.LoginResponse;

/**
 * Concept of the service needs to authenticate the users.
 */
public interface Authenticator {
    
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);

    ResponseEntity<Void> register();

}
