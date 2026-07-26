package it.unibo.controller.client.services;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 *  Contains the URI for the client.
 * @param authenticator
 * @param queries
 * @param matchmaker
 */
public record UriReader(
    @JsonProperty("authenticator") String authenticator, 
    @JsonProperty("queries") String queries, 
    @JsonProperty("matchmaker") String matchmaker) {
}