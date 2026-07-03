package it.unibo.model.movement;

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

    /**
     * Returns the current direction.
     * @return a Direction.
     */
    Direction getCurrentDirection();

    /**
     * Returns the desired direction.
     * @return a Direction.
     */
    Direction getDesiredDirection();
}
