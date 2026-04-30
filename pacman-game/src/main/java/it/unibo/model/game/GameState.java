package it.unibo.model.game;

import it.unibo.model.entities.Pacman;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public interface GameState {
    /**
     * Returns a leaderboard mapping pacmans to their score.
     */
    Map<Pacman, Integer> getLeaderboard();

    /**
     * Returns the time left.
     */
    Duration getTimeLeft();

    /**
     * Returns whether the game is considered over.
     */
    boolean isGameOver();

    /**
     * Returns the winner.
     */
    Optional<Pacman> getWinner();
}
