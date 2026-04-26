package it.unibo.model.collisions;

import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.GameEntity;

public class CircleBoundingBox implements BoundingBox {
    private final GameEntity gameEntity;
    private final int radius;

    public CircleBoundingBox(GameEntity gameEntity, int radius) {
        this.gameEntity = gameEntity;
        this.radius = radius;
    }

    private Vector2D center() {
        return gameEntity.getPosition();
    }

    @Override
    public boolean collides(BoundingBox other) {
        // Two circles intersect if the distance between their centers is less than or equal to the sum of their radii.
        if (other instanceof CircleBoundingBox c) {
            int dx = c.center().x() - center().x();
            int dy = c.center().y() - center().y();
            int radiusSum = this.radius + c.radius;
            return dx * dx + dy * dy <= radiusSum * radiusSum;
        }
        throw new IllegalArgumentException("Unknown BoundingBox type");
    }
}
