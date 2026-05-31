package it.unibo.model.movement;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.map.FourPlayersGameMapFactory;
import it.unibo.model.map.GameMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.stream.IntStream;

import static it.unibo.model.common.GameConstants.GameEntityFeatures.PACMAN;
import static it.unibo.model.common.GameConstants.TILE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementManagerTest {
    private static final int MOVEMENT_VELOCITY = PACMAN.getVelocity();
    private static final MatrixCoordinates INITIAL_MOVEMENT_POSITION = new MatrixCoordinates(3, 3);
    private static final String ROOT = "it/unibo/";
    private static final String MAPS = ROOT + "maps/";
    private static final String MOVEMENT = "/" + ROOT + "movement/";

    private final GameMap map = new FourPlayersGameMapFactory().fromJSON(MAPS + "movement_test_map.json");
    private MovementManager movement;

    private Vector2D initializeMovement(final MatrixCoordinates coordinates) {
        this.movement = new MovementManagerImpl(map, coordinates, MOVEMENT_VELOCITY);
        return map.getTile(coordinates).getCenterPosition();
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
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testSpecificDirectionMovement(Direction movementDirection) {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_POSITION);
        movement.changeDirection(movementDirection);
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 1), movement.move());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testCannotMove(Direction movementDirection) {
        Vector2D initialPosition = initializeMovement(new MatrixCoordinates(5, 7));
        movement.changeDirection(movementDirection);
        assertEquals(initialPosition, movement.move());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testMultipleTilesMovement(Direction movementDirection) {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_POSITION);
        movement.changeDirection(movementDirection);
        int numberOfMovements = (2 * TILE_SIZE) / MOVEMENT_VELOCITY;
        IntStream.range(0, numberOfMovements - 1).forEach((e) -> movement.move());
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, numberOfMovements), movement.move());
    }

    void testInvertDirection() {
        // TODO: test that the direction of movement can be inverted while moving
    }

    void testInvertDirectionAgainstWall() {
        // TODO: test that the direction of movement can be inverted even if the tile behind the starting point is a
        //  wall. Once we are back to the initial position, test that the movement does not move further, because of
        //  the wall.
    }

    void testTurn() {
        // TODO: write
    }
}
