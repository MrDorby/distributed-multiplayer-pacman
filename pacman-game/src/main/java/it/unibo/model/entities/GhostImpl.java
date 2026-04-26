package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Direction;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.Tile;

import java.util.Set;
import java.util.stream.Stream;

public class GhostImpl extends GameEntityImpl implements Ghost {

    private final static int TIME_TO_RESPAWN = 5;
    private long lastTimeDead;
    private Direction direction;

    public GhostImpl(Tile tile) {
        super(tile);
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void update(GameContext currentContext) {
        Set<Collision> collision = currentContext.getCollisions(this);
        Stream<Pacman> pacman = collision.stream().filter(x -> x.getGameEntity() instanceof Pacman).map(x -> (Pacman) x.getGameEntity());
        pacman.findFirst().ifPresent(this::checkGhostCanBeEaten);
        if (System.currentTimeMillis() - this.lastTimeDead >= TIME_TO_RESPAWN) {
            this.setIsAlive(true);
        }
        //move();
    }

    private void checkGhostCanBeEaten(Pacman pacman) {
        if (pacman.canEatGhost()) {
            this.setIsAlive(false);
            this.lastTimeDead = System.currentTimeMillis();
        }
    }

}
