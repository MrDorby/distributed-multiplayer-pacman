package it.unibo.model.map;

import java.util.Set;

/**
 * Models the concept of the map in-game.
 */
public interface Map {

    /**
     * The Set contains all the Tile in the Map.
     * @return a Set of Tiles.
     */
    Set<Tile> getTiles();

    /**
     * The Set returned contains the Tiles that refer to the pacmans' spawn points.
     * @return a Set of Tiles.
     */
    Set<Tile> getPacmanSpawnPoints();

    /**
     * A single Tile referring to the ghosts' spawn point.
     * @return a Tile.
     */
    Tile getGhostSpawnPoint();
}
