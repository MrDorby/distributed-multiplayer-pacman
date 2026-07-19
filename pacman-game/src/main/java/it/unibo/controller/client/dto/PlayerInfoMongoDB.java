package it.unibo.controller.client.dto;

/**
 * Defines the message received by the Queries Service.
 * @param id the identifier of the query.
 * @param username the player identifier.
 * @param nMatch the number of matches the player has played.
 * @param nWins the number of wins obtained by the player.
 * @param bestScore the best score obtained in the all matches.
 */
public record PlayerInfoMongoDB(
    String id,
    String username,
    int nMatch,
    int nWins,
    int bestScore) {
    
}
