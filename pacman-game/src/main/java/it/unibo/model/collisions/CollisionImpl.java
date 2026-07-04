package it.unibo.model.collisions;

import it.unibo.model.common.Direction;
import it.unibo.model.entities.GameEntity;

public class CollisionImpl implements Collision{
    private final GameEntity entity;
    private final Direction direction;

    public CollisionImpl(GameEntity entity, Direction direction) {
        this.entity = entity;
        this.direction = direction;
    }

    @Override
    public GameEntity getInvolvedEntity() {
        return entity;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }
}
