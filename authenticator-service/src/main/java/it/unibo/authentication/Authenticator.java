package it.unibo.authentication;

import org.springframework.http.ResponseEntity;

import it.unibo.LoginRequest;

/**
 * Concept of the service needs to authenticate the users.
 */
public interface Authenticator {
    
    ResponseEntity<Void> login(LoginRequest loginRequest);

    ResponseEntity<Void> register();

}
