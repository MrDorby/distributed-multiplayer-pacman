package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

/**
 * Models a GameEntity by a Factory.
 */
public interface GameEntityFactory {

    /**
     * @param spawnPoint refers to the  tile where to spawn the Pacman player.
     * @param map the game map.
     * @return a new Pacman.
     */
    Pacman createPacman(Tile spawnPoint, GameMap map);

    /**
     * @param position where to insert the dot, a Vector2D.
     * @param isSpecial whether the dot must be special or not.
     * @return a new Dot.
     */
    Dot createDot(Vector2D position, boolean isSpecial);

    /**
     * @param spawnPoint Vector2D of the ghosts' spawn point.
     * @param map the game map.
     * @return a new Ghost.
     */
    Ghost createGhost(Tile spawnPoint, GameMap map);

    /**
     * @param entity
     * @return an unmodifiable version of the GameEntity.
     */
    GameEntity unmodifiableGameEntity(GameEntity entity);
}
