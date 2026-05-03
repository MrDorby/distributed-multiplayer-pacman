package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinate;
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
    Tile getTile(MatrixCoordinate matrixPosition);
}
