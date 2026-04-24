package it.unibo.model.game;

import it.unibo.model.collisions.Collision;
import it.unibo.model.entities.GameEntity;
import it.unibo.model.entities.MovableEntity;

import java.util.Set;

public interface GameContext {
    Set<Collision> getCollisions(GameEntity entity);
    Set<GameEntity> getGameEntities();
    Set<MovableEntity> getMovableEntities();
    GameState getGameState();
}
