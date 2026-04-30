package it.unibo.model.collisions;

import it.unibo.model.common.Vector2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CircleBoundingBoxTest {

    @Test
    void shouldCollideWhenCirclesOverlap() {
        CircleBoundingBox box1 = new CircleBoundingBoxImpl(new Vector2D(0,0), 1);
        CircleBoundingBox box2 = new CircleBoundingBoxImpl(new Vector2D(1, 0), 1);
        assertTrue(box1.collides(box2));
        assertTrue(box2.collides(box1));
    }

    @Test
    void shouldCollideWhenCirclesAreTouching() {
        CircleBoundingBox box1 = new CircleBoundingBoxImpl(new Vector2D(0, 0), 1);
        CircleBoundingBox box2 = new CircleBoundingBoxImpl(new Vector2D(2, 0), 1);
        assertTrue(box1.collides(box2));
    }

    @Test
    void shouldNotCollideWhenCirclesAreSeparated() {
        CircleBoundingBox box1 = new CircleBoundingBoxImpl(new Vector2D(0, 0), 1);
        CircleBoundingBox box2 = new CircleBoundingBoxImpl(new Vector2D(3, 0), 1);
        assertFalse(box1.collides(box2));
    }

    @Test
    void shouldThrowExceptionWhenComparingAgainstUnknownBoundingBoxType() {
        CircleBoundingBox box = new CircleBoundingBoxImpl(new Vector2D(0, 0), 1);
        BoundingBox unknownBox = new BoundingBox() {
            @Override
            public boolean collides(BoundingBox other) { return false; }

            @Override
            public Vector2D getCenter() { return null; }
        };
        assertThrows(IllegalArgumentException.class, () -> box.collides(unknownBox));
    }
}
