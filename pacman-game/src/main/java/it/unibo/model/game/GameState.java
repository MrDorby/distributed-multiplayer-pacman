package it.unibo.model.game;

import java.util.Map;

public interface GameState {
    /**
     * Returns a leaderboard mapping pacmans to their score.
     */
    Map<String, Integer> getLeaderboard();

    /**
     * Returns the time left in millis.
     */
    long getTimeLeftInMillis();

    /**
     * Returns whether the game is considered over.
     */
    boolean isGameOver();

    /**
     * Returns the id of the winner.
     */
    String getWinnerId();
}
