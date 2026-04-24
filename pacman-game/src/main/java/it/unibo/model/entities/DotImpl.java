package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

public class DotImpl implements Dot {
    @Override
    public boolean isSpecial() {
        return false;
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
