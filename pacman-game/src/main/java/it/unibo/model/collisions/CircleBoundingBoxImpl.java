package it.unibo.model.collisions;

import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.GameEntity;

public class CircleBoundingBoxImpl implements CircleBoundingBox {
    private final GameEntity gameEntity;
    private final int radius;

    public CircleBoundingBoxImpl(GameEntity gameEntity, int radius) {
        this.gameEntity = gameEntity;
        this.radius = radius;
    }

    @Override
    public Vector2D getCenter() {
        return gameEntity.getPosition();
    }

    @Override
    public boolean collides(BoundingBox other) {
        // Two circles intersect if the distance between their centers is less than or equal to the sum of their radii.
        if (other instanceof CircleBoundingBoxImpl c) {
            int dx = c.getCenter().x() - getCenter().x();
            int dy = c.getCenter().y() - getCenter().y();
            int radiusSum = this.radius + c.radius;
            return dx * dx + dy * dy <= radiusSum * radiusSum;
        }
        throw new IllegalArgumentException("Unknown BoundingBox type");
    }

    @Override
    public int getRadius() {
        return radius;
    }
}
