package it.unibo.model.game;

import it.unibo.model.entities.Pacman;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public interface GameState {
    Map<Pacman, Integer> getLeaderboard();
    Duration getTimeLeft();
    boolean isGameOver();
    Optional<Pacman> getWinner();
}
