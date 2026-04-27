package it.unibo.model.collisions;

import it.unibo.model.common.Vector2D;

public interface BoundingBox {
    /**
     * Returns whether the current bounding box collides with another.
     */
    boolean collides(BoundingBox other);

    /**
     * Returns the center of the bounding box.
     */
    Vector2D getCenter();
}
