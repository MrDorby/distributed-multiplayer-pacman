package it.unibo.view.viewmodel;

import it.unibo.model.common.Vector2D;

public record GhostViewModel(
        Vector2D position,
        String currentDirection,
        boolean isAlive
) {}