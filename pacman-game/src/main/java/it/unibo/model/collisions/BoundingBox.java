package it.unibo.model.collisions;

public interface BoundingBox {
    /**
     * Returns whether the current bounding box collides with another.
     */
    boolean collides(BoundingBox other);
}
