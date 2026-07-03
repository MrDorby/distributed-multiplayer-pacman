package it.unibo.controller.shared.network.dto;

public record PacmanDTO(
        String id,
        int score,
        int lives,
        boolean controlledByPlayer,
        boolean isAlive,
        boolean canEatGhosts,
        boolean isInvincible,
        long lastTimeSpecialDotWasEaten,
        long lastTimeBecameInvincible,
        long lastTimeDirectionWasChanged,
        String currentDirection,
        String desiredDirection,
        int targetTileRow,
        int targetTileCol,
        int currentTileRow,
        int currentTileCol,
        int x,
        int y
) {}