package it.unibo.dto;

import java.security.PublicKey;

/**
 * Content of the user's login response. It contains the token.
 */
public record LoginResponse(String token, PublicKey authPublicKey) {
    
}
