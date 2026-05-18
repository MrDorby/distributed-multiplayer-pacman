package it.unibo;

import org.springframework.http.ResponseEntity;

/**
 * Concept of the service needs to authenticate the users.
 */
public interface Authenticator {
    
    ResponseEntity<Void> login(LoginRequest loginRequest);

    ResponseEntity<Void> register();

}
