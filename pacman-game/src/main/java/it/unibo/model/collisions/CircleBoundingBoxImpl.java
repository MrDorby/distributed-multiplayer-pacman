package it.unibo.model.collisions;

import it.unibo.model.common.Vector2D;

public record CircleBoundingBoxImpl(Vector2D center, int radius) implements CircleBoundingBox {

    @Override
    public boolean collides(BoundingBox other) {
        // Two circles intersect if the distance between their centers is less than or equal to the sum of their radii.
        if (other instanceof CircleBoundingBox otherCircle) {
            int dx = otherCircle.center().x() - center.x();
            int dy = otherCircle.center().y() - center.y();
            int radiusSum = this.radius + otherCircle.radius();
            return dx * dx + dy * dy <= radiusSum * radiusSum;
        }
        throw new IllegalArgumentException("Unknown BoundingBox type");
    }
}
