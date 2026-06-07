package it.unibo.model.entities;

import java.util.Random;

import it.unibo.model.common.Direction;

/**
 * Models the Entity that moves during a game.
 */
public interface MovableEntity extends GameEntity {
    
    /**
     * @return the direction where the entity want to moves.
     */
    Direction getDirection();

    /**
     * @return a randomic direction.
     */
    static Direction getRandomDirection() {
        return Direction.values()[new Random().nextInt(0, 4)];
    }
}
