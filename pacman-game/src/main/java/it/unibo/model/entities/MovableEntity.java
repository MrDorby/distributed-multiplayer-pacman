package it.unibo.model.entities;

import it.unibo.model.common.Direction;

public interface MovableEntity extends GameEntity {
    Direction getDirection();
}
