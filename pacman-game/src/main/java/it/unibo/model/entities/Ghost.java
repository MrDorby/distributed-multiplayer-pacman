package it.unibo.model.entities;

import it.unibo.model.common.MatrixCoordinates;

/**
 *  Models the concept of the ghost.
 */
public interface Ghost extends MovableEntity {

    /**
     * @return the value in points for the ghost.
     */
    int getGhostValue();

    /**
     * Returns position of the ghost in the matrix grid.
     */
    MatrixCoordinates getMatrixCoordinates();
}
