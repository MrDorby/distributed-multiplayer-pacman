package it.unibo.model.map;

import java.util.Set;

/**
 * Models the concept of the map in-game.
 */
public interface Map {

    /**
     * The Set returned contains the tiles that refer to the pacmans' spawn points.
     * @return a Set of Tiles.
     */
    Set<Tile> getPacmanSpawnPoints();

    /**
     * A single tile referring to the ghosts' spawn point.
     * @return a Tile.
     */
    Tile getGhostSpawnPoint();
}
