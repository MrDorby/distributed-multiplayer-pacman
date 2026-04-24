package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

public interface GameEntity {
    void update(GameContext currentContext);
    Vector2D getPosition();
    BoundingBox getBoundingBox();
    boolean isAlive();
}
