package it.unibo.controller.network.dto;

import java.util.Map;

public record GameStateDTO(
        Map<String, Integer> leaderboard,
        long timeLeftInMillis,
        boolean isGameOver,
        String winnerId
) {}
