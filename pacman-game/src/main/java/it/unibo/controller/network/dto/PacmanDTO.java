package it.unibo.controller.network.dto;

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
        String currentDirection, // Unused for now when recreating a PacmanImpl
        // String desiredDirection,
        int tileRow,
        int tileCol,
        int x,
        int y
) {}