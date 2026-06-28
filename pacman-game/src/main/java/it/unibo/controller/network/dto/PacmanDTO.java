package it.unibo.controller.network.dto;

public record PacmanDTO(
        String id,
        int score,
        int lives,
        boolean controlledByPlayer,
        boolean canEatGhosts,
        long whenSpecialDotWasEaten,
        boolean isInvincible,
        long whenBecameInvincible,
        String currentDirection,
        // String desiredDirection,
        int tileRow,
        int tileCol,
        int x,
        int y,
        long lastTimeDirectionWasChanged,
        boolean isAlive
) {}
