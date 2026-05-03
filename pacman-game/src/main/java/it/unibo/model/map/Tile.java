package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinate;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;

import java.util.Optional;

/**
 * Models the concept of a cell inside the map.
 */
public interface Tile {

    /**
     * @return a boolean true if the Tile is a Wall or false otherwise.
     */
    boolean isWall(); // TODO: keep?

    /**
     * @return the Dot contained in the Tile, if present, an empty Optional otherwise.
     */
    Optional<Dot> getDot();

    /**
     * Gives the position of the center of the Tile.
     * @return a Vector2D corresponding to the coordinates of the center.
     */
    Vector2D getCenterPosition();

    /**
     * Gives the position of the Tile inside the Map matrix.
     * @return a Vector2D corresponding to the matrix coordinates of the Tile.
     */
    MatrixCoordinate getMatrixPosition();

    /**
     * @return the TileType of the corresponding Tile.
     */
    TileType getTileType(); //TODO: keep? In that case, isWall() is not needed.
}
