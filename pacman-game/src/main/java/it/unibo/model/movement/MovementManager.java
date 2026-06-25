package it.unibo.model.movement;

import java.util.List;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;

/**
 * Manager for the calculus of the MovableEntity's movement.
 */
public interface MovementManager {

    /**
     * Elaborates the next position for the Movable Entity.
     * @return a Vector2D.
     */
    Vector2D move();

    /**
     * Memorizes the next direction chosen for the Movable Entity.
     * @param direction the next direction.
     */
    void changeDirection(Direction direction);

    /**
     * Shows the current matrix coordinates of the entity.
     * @return a MatrixCoordinates.
     */
    MatrixCoordinates currentMatrixCoordinates();

    // TODO: delete this method
    /**
     * Shows the possible directions to move.
     * @param matrixCoordinates the matrix coordinates where the game entity is.
     * @return a list of all available directions.
     */
    List<Direction> getWalkableDirection(MatrixCoordinates matrixCoordinates);
}
