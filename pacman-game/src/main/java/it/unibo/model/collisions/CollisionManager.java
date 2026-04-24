package it.unibo.model.collisions;

import it.unibo.model.entities.GameEntity;

import java.util.Map;
import java.util.Set;

public interface CollisionManager {
    Map<GameEntity, Set<Collision>> computeCollisions();
}
