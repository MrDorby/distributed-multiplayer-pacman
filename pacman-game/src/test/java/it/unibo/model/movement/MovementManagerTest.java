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

import static it.unibo.model.common.Direction.*;
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

    // TODO: check the correctness of every test

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

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testInvertDirection(Direction movementDirection) {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_POSITION);
        movement.changeDirection(movementDirection);
        movement.move();
        Vector2D newPosition = movement.move();
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 2), newPosition);
        movement.changeDirection(movementDirection.getOpposite());
        IntStream.range(0, 3).forEach((e) -> movement.move());
        assertEquals(getExpectedFinalPosition(newPosition, movementDirection.getOpposite(), 4), movement.move());
    }

    @ParameterizedTest
    @CsvSource({
            "UP, 5, 1",
            "DOWN, 1, 5",
            "LEFT, 1, 5",
            "RIGHT, 5, 1",
            "NONE, 5, 1"
    })
    void testInvertDirectionAgainstWall(Direction movementDirection, int startingRow, int startingColumn) {
        Vector2D initialPosition = initializeMovement(new MatrixCoordinates(startingRow, startingColumn));
        movement.changeDirection(movementDirection);
        movement.move();
        Vector2D newPosition = movement.move();
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 2), newPosition);
        movement.changeDirection(movementDirection.getOpposite());
        movement.move();
        assertEquals(initialPosition, movement.move());
        assertEquals(initialPosition, movement.move());
        assertEquals(initialPosition, movement.move());
    }

    private Direction getRightTurn(Direction direction) {
        return switch (direction) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
            case NONE -> NONE;
        };
    }

    @ParameterizedTest
    @CsvSource({
            "UP, 4, 3",
            "DOWN, 2, 3",
            "LEFT, 3, 4",
            "RIGHT, 3, 2",
            "NONE, 3, 3"
    })
    void testTurn(Direction movementDirection, int startingRow, int startingColumn) {
        // TODO: write
        //  Make so that, for each starting position and direction, the test verifies both a left-hand turn and a
        //  right-hand turn.
    }
}
