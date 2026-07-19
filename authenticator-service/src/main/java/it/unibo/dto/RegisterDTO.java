package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Content of the user's register request.
 * @param username
 * @param password
 */
public record RegisterDTO(
    @JsonProperty("username") String username, 
    @JsonProperty("password") String password) {
}