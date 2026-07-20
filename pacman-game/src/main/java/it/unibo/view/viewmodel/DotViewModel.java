package it.unibo.view.viewmodel;

import it.unibo.model.common.Vector2D;

public record DotViewModel(
        Vector2D position,
        boolean isAlive,
        boolean isSpecial
) {}