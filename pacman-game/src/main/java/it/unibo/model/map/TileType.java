package it.unibo.model.map;

/**
 * The type of Tile.
 */
public enum TileType {
    /** A walkable Tile that may contain a Dot */
    SIMPLE,
    /** A wall Tile */
    WALL,
    /** A Pac-Man spawn point */
    PACMAN_SPAWN,
    /** A ghost spawn point */
    GHOST_SPAWN,
}
