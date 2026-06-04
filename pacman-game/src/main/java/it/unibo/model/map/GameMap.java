package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;

import java.util.Set;

/**
 * Models the concept of the map in-game.
 */
public interface GameMap {

    /**
     * @return the Set of Tiles containing the Pac-Man spawn points.
     */
    Set<Tile> getPacmanSpawnPoints();

    /**
     * @return the Tile representing the ghosts' spawn point.
     */
    Tile getGhostSpawnPoint();

    /**
     * Returns the Tile at the specified position in the map's matrix.
     * @param matrixPosition the matrix position of the Tile.
     * @return the Tile at the specified position.
     * @throws IndexOutOfBoundsException in case the specified position is out of bounds.
     */
    Tile getTile(MatrixCoordinates matrixPosition);

    /**
     * @return a Set containing all the Map's Tiles.
     */
    Set<Tile> getTiles();

    /**
     * @return the size of the map's grid (number of rows and columns).
     */
    MatrixCoordinates getGridSize();

    /**
     * @return the number of positions in the map (x and y).
     */
    Vector2D getSize();
}
