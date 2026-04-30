package it.unibo.model.collisions;

import it.unibo.model.entities.GameEntity;
import it.unibo.model.game.GameContext;

import java.util.Map;
import java.util.Set;

public interface CollisionManager {
    /**
     * Returns a map with of collision where a game entity is mapped to its set of collisions with other game entities.
     */
    Map<GameEntity, Set<Collision>> computeCollisions(GameContext context);
}
