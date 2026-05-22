package it.unibo.model.movement;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.map.FourPlayersGameMapFactory;
import it.unibo.model.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static it.unibo.model.common.GameConstants.GameEntityFeatures.PACMAN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementManagerTest {
    private final MatrixCoordinates initialCoordinates = new MatrixCoordinates(3, 3);
    private final GameMap map = new FourPlayersGameMapFactory().fromJSON("it/unibo/maps/movement_test_map.json");
    private final Vector2D initialPosition = map.getTile(initialCoordinates).getCenterPosition(); // TODO: generalize the initial position
    private static final int MOVEMENT_VELOCITY = PACMAN.getVelocity();

    private MovementManager movement;

    @BeforeEach
    void init() {
        this.movement = new MovementManagerImpl(map, initialCoordinates, MOVEMENT_VELOCITY);
    }

    private Vector2D getExpectedFinalPosition(
            Vector2D initialPosition,
            Direction movementDirection,
            int numberOfMovements) {
        return switch (movementDirection) {
            case UP -> new Vector2D(initialPosition.x(), initialPosition.y() - (MOVEMENT_VELOCITY * numberOfMovements));
            case DOWN -> new Vector2D(initialPosition.x(), initialPosition.y() + (MOVEMENT_VELOCITY * numberOfMovements));
            case RIGHT -> new Vector2D(initialPosition.x() + (MOVEMENT_VELOCITY * numberOfMovements), initialPosition.y());
            case LEFT -> new Vector2D(initialPosition.x() - (MOVEMENT_VELOCITY * numberOfMovements), initialPosition.y());
            case NONE -> initialPosition;
        };
    }

    @ParameterizedTest
    @CsvSource({
            "UP",
            "DOWN",
            "LEFT",
            "RIGHT"
    })
    void testSpecificDirectionMovement(Direction movementDirection) {
        movement.changeDirection(movementDirection);
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 1), movement.move());
    }

    void testCannotMove() {
        // TODO: test that the movement is not allowed towards walls
    }

    void testInvertDirection() {
        // TODO: test that the direction of movement can be inverted while moving
    }

    void testTurn() {
        // TODO: write
    }
}
