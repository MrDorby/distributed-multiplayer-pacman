package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.Tile;

public abstract class GameEntityImpl implements GameEntity {

    private BoundingBox boundingBox;
    private Vector2D position;
    private boolean alive;

    public GameEntityImpl(Tile tile) {
        //this.boundingBox = new BoundingBoxImpl();
        this.position = tile.getCenterPosition();
        this.alive = true;
    }

    @Override
    public abstract void update(GameContext currentContext);

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    protected void setPosition(Vector2D position) {
        this.position = position;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public boolean isAlive() {
        return this.alive;
    }

    @Override
    public void setIsAlive(boolean isAlive) {
        this.alive = isAlive;
    }
}