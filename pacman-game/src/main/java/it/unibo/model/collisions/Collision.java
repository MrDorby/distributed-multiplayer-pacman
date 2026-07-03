package it.unibo.model.collisions;

import it.unibo.model.common.Direction;
import it.unibo.model.entities.GameEntity;

public interface Collision {
    /**
     * Returns the entity involved in the collision.
     */
    GameEntity getInvolvedEntity();

    /**
     * Returns the direction of the collision with the game entity.
     */
    Direction getDirection();
}
