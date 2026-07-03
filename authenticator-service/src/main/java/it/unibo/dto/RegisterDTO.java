package it.unibo.dto;

/**
 * Content of the user's register request.
 * @param username
 * @param password
 */
public record RegisterDTO(String username, String password) {
}