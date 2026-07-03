package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

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
            checkCollisions(currentContext);
        } else {
            handleRespawn(currentContext);
        }
    }

    private void checkCollisions(GameContext currentContext) {
        currentContext.getCollisions(this).stream()
                .filter(collision -> collision.getInvolvedEntity() instanceof Pacman)
                .findFirst()
                .ifPresent(_ -> markAsEaten(currentContext));
    }

    private void markAsEaten(GameContext context) {
        this.setIsAlive(false);
        this.lastTimeEaten = context.getGameState().getTimeLeftInMillis();
    }

    protected void handleRespawn(GameContext currentContext) {
        long currentTime = currentContext.getGameState().getTimeLeftInMillis();
        if (currentTime <= this.lastTimeEaten - TIME_TO_RESPAWN_IN_MILLIS) {
            this.setIsAlive(true);
        }
    }
}