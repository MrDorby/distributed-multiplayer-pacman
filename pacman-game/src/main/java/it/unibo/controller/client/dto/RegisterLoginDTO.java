package it.unibo.controller.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RegisterLoginDTO is the message exchanged during login and register phases.
 * @param username of the user.
 * @param password of the user matching the specified username.
 */
public record RegisterLoginDTO(
    @JsonProperty("username") String username, 
    @JsonProperty("password") String password) {
    
}
