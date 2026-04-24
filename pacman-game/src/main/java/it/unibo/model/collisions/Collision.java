package it.unibo.model.collisions;

import it.unibo.model.common.Direction;
import it.unibo.model.entities.GameEntity;

public interface Collision {
    GameEntity getGameEntity();
    Direction getDirection();
}
