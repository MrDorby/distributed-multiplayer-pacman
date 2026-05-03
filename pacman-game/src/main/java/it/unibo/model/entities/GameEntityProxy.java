package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

public class GameEntityProxy implements GameEntity {

    private final boolean isAlive;
    private final BoundingBox boundingBox;
    private final Vector2D position;

    public GameEntityProxy(GameEntity gameEntity) {
        this.isAlive = gameEntity.isAlive();
        this.boundingBox = gameEntity.getBoundingBox();
        this.position = gameEntity.getPosition();
    }

    @Override
    public void update(GameContext currentContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public boolean isAlive() {
        return this.isAlive;
    }

    @Override
    public void setIsAlive(boolean isAlive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPosition(Vector2D position) {
        throw new UnsupportedOperationException();
    }
}
