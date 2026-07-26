package it.unibo.view.viewmodel;

import it.unibo.model.common.Vector2D;

public record PacmanViewModel(
        String id,
        Vector2D position,
        String currentDirection,
        boolean isAlive,
        boolean isInvincible,
        boolean canEatGhosts,
        boolean controlledByPlayer,
        int score,
        int lives
) {}