package it.unibo.view.viewmodel;

import java.util.Map;

public record GameStateViewModel(
        Map<String, Integer> leaderboard,
        long timeLeftInMillis,
        boolean isGameOver
) {}