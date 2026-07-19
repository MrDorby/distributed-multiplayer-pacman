package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Content of the user's login request.
 * @param username
 * @param password
 */
public record LoginDTO(
    @JsonProperty("username") String username, 
    @JsonProperty("password") String password) {
    
}
