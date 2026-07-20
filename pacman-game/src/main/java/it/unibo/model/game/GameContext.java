package it.unibo.model.game;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.entities.*;
import it.unibo.model.map.GameMap;

import java.util.Map;
import java.util.Set;

public interface GameContext {

    long getTick();

    void setTick(long tick);

    /**
     * Returns the latest calculated collisions.
     */
    Set<Collision> getCollisions(GameEntity entity);

    /**
     * Overrides the current collisions with the new ones.
     */
    void setCollisions(Map<GameEntity, Set<Collision>> collisions);

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
    GameMap getMap();

    /**
     * Returns a map from MatrixCoordinates to Dots
     */
    Map<MatrixCoordinates, Dot> getDotsMap();

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
     * Decreases time from time left.
     */
    void decrementTime(long deltaInMillis);

    /**
     * Calculates the current game state.
     */
    void createGameState();
}
