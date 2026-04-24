package it.unibo.model.game;

import it.unibo.model.entities.Pacman;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class GameStateImpl implements GameState {
    @Override
    public Map<Pacman, Integer> getLeaderboard() {
        return Map.of();
    }

    @Override
    public Duration getTimeLeft() {
        return null;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public Optional<Pacman> getWinner() {
        return Optional.empty();
    }
}
