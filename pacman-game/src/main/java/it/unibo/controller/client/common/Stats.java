package it.unibo.controller.client.common;

/**
 * Contains the statistics of the user.
 * @param username the player identifier.
 * @param nMatch the number of matches played.
 * @param nWins the number of wins done.
 * @param winRate the ratio nWins / nMatch.
 * @param bestScore the best score of the player.
 */
public record Stats(
    String username,
    int nMatch,
    int nWins,
    float winRate,
    int bestScore) {
    
}
