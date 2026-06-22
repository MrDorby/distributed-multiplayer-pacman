package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.collisions.CircleBoundingBoxImpl;
import it.unibo.model.common.GameConstants;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.Tile;

public abstract class GameEntityImpl implements GameEntity {

    private BoundingBox boundingBox;
    private Vector2D position;
    private boolean alive = true;

    public GameEntityImpl(Tile tile) {
        this.position = tile.getCenterPosition();
        this.boundingBox = new CircleBoundingBoxImpl(position, getRadiusFromGameEntity());
    }

    public GameEntityImpl(Vector2D position) {
        this.position = position;
        this.boundingBox = new CircleBoundingBoxImpl(position, getRadiusFromGameEntity());
    }

    private int getRadiusFromGameEntity() {
        if (this instanceof Pacman) {
            return GameConstants.GameEntityFeatures.PACMAN.getRadius();
        } else if (this instanceof Ghost) {
            return GameConstants.GameEntityFeatures.GHOST.getRadius();
        } else {
            return GameConstants.GameEntityFeatures.DOT.getRadius();
        }
    }

    @Override
    public abstract void update(GameContext currentContext);

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Vector2D position) {
        this.position = position;
        this.boundingBox = new CircleBoundingBoxImpl(position, getRadiusFromGameEntity());
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