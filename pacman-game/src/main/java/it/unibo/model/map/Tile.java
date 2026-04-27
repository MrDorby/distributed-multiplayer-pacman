package it.unibo.model.map;

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
    boolean isWall();

    /**
     * This method let you get the dot ì, if present, inside the tile.
     * @return an Optional of Dot.
     */
    Optional<Dot> getDot();

    /**
     * Gives the position of the center of the Tile.
     * @return a Vector2D corresponding to the coordinates of the center.
     */
    Vector2D getCenterPosition();

    /**
     * Gives the position of the Tile inside the matrix (Map).
     * @return a Vector2D corresponding to the coordinates of the Tile.
     */
    Vector2D getMatrixPosition();

    /**
     * @return the TileType of the corresponding Tile.
     */
    TileType getTileType();
}
