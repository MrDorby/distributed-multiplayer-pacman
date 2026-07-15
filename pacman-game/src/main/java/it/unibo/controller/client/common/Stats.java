package it.unibo.controller.client.common;

/**
 * 
 * Stats
 * @param username
 * @param nMatch
 * @param nWins
 * @param winRate
 * @param bestScore
 */
public record Stats(
    String username,
    int nMatch,
    int nWins,
    float winRate,
    int bestScore) {
    
}
