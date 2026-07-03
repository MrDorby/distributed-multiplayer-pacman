package it.unibo.dto;

/**
 * Content of the user's login request.
 * @param username
 * @param password
 */
public record LoginDTO(String username, String password) {
    
}
