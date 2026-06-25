package it.unibo.model.map;

/**
 * The type of Tile.
 */
public enum TileType {
    // TODO: Differentiate between EMPTY, DOT and SPECIAL_DOT with specific types.
    /** A walkable Tile that may contain a Dot */
    SIMPLE,
    /** A wall Tile */
    WALL,
    /** A Pac-Man spawn point */
    PACMAN_SPAWN,
    /** A ghost spawn point */
    GHOST_SPAWN,
}
