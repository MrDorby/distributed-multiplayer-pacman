package it.unibo.model.entities;

import it.unibo.model.map.Tile;

/**
 * Models a GameEntity by a Factory.
 */
public interface GameEntityFactory {

    /**
     * @param spawnPoint refers to the  tile where to spawn the Pacman player.
     * @return a new Pacman.
     */
    Pacman createPacman(Tile spawnPoint);

    /**
     * @param tile where to insert the dot.
     * @return a new Dot.
     */
    Dot createDot(Tile tile);

    /**
     * @return a new Ghost.
     */
    Ghost createGhost(Tile spawnPoint);

    /**
     * @param entity
     * @return an unmodifiable version of the GameEntity.
     */
    GameEntity unmodifiableGameEntity(GameEntity entity);
}
