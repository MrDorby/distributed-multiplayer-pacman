package it.unibo.model.map;

/**
 * The type of Tile.
 */
public enum TileType {
    /** A walkable Tile that does not contain any Dot */
    EMPTY,
    /** A walkable Tile that contains a simple Dot */
    DOT,
    /** A walkable Tile that contains a special Dot */
    SPECIAL_DOT,
    /** A wall Tile */
    WALL,
    /** A Pac-Man spawn point */
    PACMAN_SPAWN,
    /** A ghost spawn point */
    GHOST_SPAWN,
}
