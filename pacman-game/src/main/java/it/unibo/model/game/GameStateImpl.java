package it.unibo.model.game;

import it.unibo.model.entities.Pacman;

import java.util.Map;
import java.util.Optional;

public class GameStateImpl implements GameState {
    private final Map<Pacman, Integer> leaderboard;
    private final long timeLeftInMillis;
    private final boolean isGameOver;
    private final Pacman winner;

    public GameStateImpl(Map<Pacman, Integer> leaderboard, long timeLeftInMillis, boolean isGameOver, Pacman winner) {
        this.leaderboard = leaderboard;
        this.timeLeftInMillis = timeLeftInMillis;
        this.isGameOver = isGameOver;
        this.winner = winner;
    }

    @Override
    public Map<Pacman, Integer> getLeaderboard() {
        return leaderboard;
    }

    @Override
    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
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
