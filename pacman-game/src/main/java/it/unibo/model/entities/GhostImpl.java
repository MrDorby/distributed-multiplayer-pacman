package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Direction;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

public class GhostImpl implements Ghost {
    @Override
    public Direction getDirection() {
        return null;
    }

    @Override
    public void update(GameContext currentContext) {

    }

    @Override
    public Vector2D getPosition() {
        return null;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public boolean isAlive() {
        return false;
    }
}
