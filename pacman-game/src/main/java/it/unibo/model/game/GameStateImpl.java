package it.unibo.model.game;

import java.util.Map;

public class GameStateImpl implements GameState {
    private final Map<String, Integer> leaderboard;
    private final long timeLeftInMillis;
    private final boolean isGameOver;
    private final String winnerId;

    public GameStateImpl(Map<String, Integer> leaderboard, long timeLeftInMillis, boolean isGameOver, String winnerId) {
        this.leaderboard = leaderboard;
        this.timeLeftInMillis = timeLeftInMillis;
        this.isGameOver = isGameOver;
        this.winnerId = winnerId;
    }

    @Override
    public Map<String, Integer> getLeaderboard() {
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
    public String getWinnerId() {
        return winnerId;
    }
}
