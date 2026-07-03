package it.unibo.dto;

/**
 * Content of the user's login response. It contains the token.
 * @param token
 */

public record LoginResponse(String token) {
    
}
