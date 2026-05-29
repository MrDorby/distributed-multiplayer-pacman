package it.unibo.dto;

/**
 * Content of the user's register request.
 */
public record RegisterDTO(String email, String username, String password) {
}