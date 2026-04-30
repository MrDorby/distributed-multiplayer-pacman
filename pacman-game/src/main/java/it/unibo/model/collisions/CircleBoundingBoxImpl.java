package it.unibo.model.collisions;

import it.unibo.model.common.Vector2D;

public class CircleBoundingBoxImpl implements CircleBoundingBox {
    private final Vector2D center;
    private final int radius;

    public CircleBoundingBoxImpl(Vector2D center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Vector2D getCenter() {
        return center;
    }

    @Override
    public boolean collides(BoundingBox other) {
        // Two circles intersect if the distance between their centers is less than or equal to the sum of their radii.
        if (other instanceof CircleBoundingBox otherCircle) {
            int dx = otherCircle.getCenter().x() - center.x();
            int dy = otherCircle.getCenter().y() - center.y();
            int radiusSum = this.radius + otherCircle.getRadius();
            return dx * dx + dy * dy <= radiusSum * radiusSum;
        }
        throw new IllegalArgumentException("Unknown BoundingBox type");
    }

    @Override
    public int getRadius() {
        return radius;
    }
}
