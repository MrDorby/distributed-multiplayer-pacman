package it.unibo.model.game;

import it.unibo.model.entities.Pacman;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class GameStateImpl implements GameState {
    private final Map<Pacman, Integer> leaderboard;
    private final Duration timeLeft;
    private final boolean isGameOver;
    private final Pacman winner;

    public GameStateImpl(Map<Pacman, Integer> leaderboard, Duration timeLeft, boolean isGameOver, Pacman winner) {
        this.leaderboard = leaderboard;
        this.timeLeft = timeLeft;
        this.isGameOver = isGameOver;
        this.winner = winner;
    }

    @Override
    public Map<Pacman, Integer> getLeaderboard() {
        return leaderboard;
    }

    @Override
    public Duration getTimeLeft() {
        return timeLeft;
    }

    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public Optional<Pacman> getWinner() {
        return Optional.ofNullable(winner);
    }
}
