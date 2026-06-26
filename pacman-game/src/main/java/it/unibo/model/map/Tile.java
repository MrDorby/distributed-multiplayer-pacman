package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;

/**
 * Models the concept of a cell inside the map.
 */
public interface Tile {
    /**
     * Gives the position of the center of the Tile.
     * @return a Vector2D corresponding to the coordinates of the center.
     */
    Vector2D getCenterPosition();

    /**
     * Gives the position of the Tile inside the Map matrix.
     * @return a Vector2D corresponding to the matrix coordinates of the Tile.
     */
    MatrixCoordinates getMatrixPosition();

    /**
     * @return the TileType of the corresponding Tile.
     */
    TileType getTileType();
}
