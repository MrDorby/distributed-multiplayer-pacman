package it.unibo.model.game;

import it.unibo.model.collisions.Collision;
import it.unibo.model.entities.*;
import it.unibo.model.map.Map;

import java.util.Set;

public interface GameContext {

    /**
     * Returns the latest calculated collisions.
     */
    Set<Collision> getCollisions(GameEntity entity);

    /**
     * Overrides the current collisions with the new ones.
     */
    void setCollisions(Set<Collision> collisions);

    /**
     * Returns every game entity of the domain.
     */
    Set<GameEntity> getGameEntities();

    /**
     * Returns the movable entities (e.g. ghosts and pacmans)
     */
    Set<MovableEntity> getMovableEntities();

    /**
     * Returns the current map.
     */
    Map getMap();

    /**
     * Returns the dots on the map.
     */
    Set<Dot> getDots();

    /**
     * Returns the ghosts.
     */
    Set<Ghost> getGhosts();

    /**
     * Returns the pacmans.
     */
    Set<Pacman> getPacmans();

    /**
     * Returns current game state (results).
     */
    GameState getGameState();

    /**
     * Calculates the current game state.
     */
    void createGameState();
}
