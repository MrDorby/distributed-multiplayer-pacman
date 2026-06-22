package it.unibo.model.entities;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

import java.util.Set;

public class DotImpl extends GameEntityImpl implements Dot {

    private final static int TIME_TO_RESPAWN = 5000;  // 5000 millis
    private final static int DOT_VALUE = 1;
    private boolean isSpecial;
    private long lastTimeDead;

    public DotImpl(Vector2D position) {
        super(position);
    }

    @Override
    public boolean isSpecial() {
        return this.isSpecial;
    }

    @Override
    public void setIsSpecial(boolean isSpecial) {       //TODO: Who sets it?
        this.isSpecial = isSpecial;
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
            long currentTime = currentContext.getGameState().getTimeLeft().toMillis();
            if (currentTime <= this.lastTimeDead - TIME_TO_RESPAWN) {
                this.setIsAlive(true);
            }
        }
    }

    private void hideDot(GameContext context) {
        this.setIsAlive(false);
        this.lastTimeDead = context.getGameState().getTimeLeft().toMillis();
    }
}
