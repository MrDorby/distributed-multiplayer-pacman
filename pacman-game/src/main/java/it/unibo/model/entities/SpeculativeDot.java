package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

/**
 * This {@code Dot} implementation won't be marked as eaten when touched by a pacman, but it is able to respawn.
 */
public class SpeculativeDot extends DotImpl {
    public SpeculativeDot(Vector2D position, boolean isSpecial) {
        super(position, isSpecial);
    }

    public SpeculativeDot(Vector2D position) {
        super(position);
    }

    @Override
    public void update(GameContext currentContext) {
        if (!this.isAlive()) {
            this.handleRespawn(currentContext);
        }
    }
}
