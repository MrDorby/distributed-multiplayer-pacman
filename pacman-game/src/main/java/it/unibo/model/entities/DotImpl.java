package it.unibo.model.entities;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

import java.util.Set;

public class DotImpl extends GameEntityImpl implements Dot {
    private final static int TIME_TO_RESPAWN_IN_MILLIS = 5000;
    private final static int DOT_VALUE = 1;
    private final boolean isSpecial;
    private long lastTimeEaten;

    public DotImpl(Vector2D position, boolean isSpecial) {
        super(position);
        this.isSpecial = isSpecial;
    }

    public DotImpl(Vector2D position) {
        this(position, false);
    }

    @Override
    public boolean isSpecial() {
        return this.isSpecial;
    }

    @Override
    public int dotValue() {
        return DOT_VALUE;
    }

    @Override
    public void update(GameContext currentContext) {
        if (this.isAlive()) {
            Set<Collision> collision = currentContext.getCollisions(this);
            collision.stream()
                    .filter(x -> x.getGameEntity() instanceof Pacman)
                    .findFirst()
                    .ifPresent(_ -> hideDot(currentContext));
        } else {
            long currentTime = currentContext.getGameState().getTimeLeftInMillis();
            if (currentTime <= this.lastTimeEaten - TIME_TO_RESPAWN_IN_MILLIS) {
                this.setIsAlive(true);
            }
        }
    }

    private void hideDot(GameContext context) {
        this.setIsAlive(false);
        this.lastTimeEaten = context.getGameState().getTimeLeftInMillis();
    }
}
