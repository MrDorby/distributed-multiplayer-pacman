package it.unibo.controller.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterLoginDTO(
    @JsonProperty("username") String username, 
    @JsonProperty("password") String password) {
    
}
