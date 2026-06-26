package it.unibo.model.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.map.GameMap;

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

    /**
     * Obtains a map for the available directions.
     * @param matrixCoordinates the actual position of the game entity.
     * @param map the game map.
     * @return a Map<MatrixCoordinates, Direction>.
     */
    static Map<MatrixCoordinates, Direction> getWalkableDirection(MatrixCoordinates matrixCoordinates, GameMap map) {
        Map<MatrixCoordinates, Direction> md = new HashMap<>();
        for (int i = 0; i < Direction.values().length - 1; i++) {
            Direction dir = Direction.values()[i];
            MatrixCoordinates mtx = matrixCoordinates.getNeighbour(dir, map.getGridSize());
            if (mtx != matrixCoordinates) {
                md.put(mtx, dir);
            }
        }
        return md;
    }
}
