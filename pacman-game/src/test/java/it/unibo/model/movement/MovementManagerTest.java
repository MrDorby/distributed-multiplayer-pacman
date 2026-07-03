package it.unibo.model.movement;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.map.FourPlayersGameMapFactory;
import it.unibo.model.map.GameMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import static it.unibo.model.common.Direction.*;
import static it.unibo.model.common.GameConstants.GameEntityFeatures.PACMAN;
import static it.unibo.model.common.GameConstants.TILE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementManagerTest {
    private static final int MOVEMENT_VELOCITY = PACMAN.getVelocity();
    private static final int MOVEMENTS_IN_A_TILE = TILE_SIZE / MOVEMENT_VELOCITY;
    private static final MatrixCoordinates INITIAL_MOVEMENT_COORDINATES = new MatrixCoordinates(3, 3);
    private static final String ROOT = "it/unibo/";
    private static final String MAPS = ROOT + "maps/";
    private static final String MOVEMENT = "/" + ROOT + "movement/";
    private static final String MOVEMENT_TEST_MAP = MAPS + "movement_test_map.json";
    private static final String TOROID_TEST_MAP = MAPS + "toroid_test_map.json";
    private static final String TOROID_WITH_WALLS_TEST_MAP = MAPS + "toroid_with_walls_test_map.json";

    private GameMap map;
    private MovementManager movement;

    /**
     * Initializes a MovementManager in the specified initial coordinates, returning the corresponding position vector
     * in the map.
     * @param coordinates the initial coordinates of the MovementManager.
     * @return the vector position that corresponds to the given coordinates on the test map.
     */
    private Vector2D initializeMovement(final MatrixCoordinates coordinates, String mapPath) {
        this.map = new FourPlayersGameMapFactory().fromJSON(mapPath);
        this.movement = new MovementManagerImpl(map, coordinates, MOVEMENT_VELOCITY);
        return map.getTile(coordinates).getCenterPosition();
    }

    /**
     * Calculates the expected final vector position after a movement.
     * @param initialPosition the initial position of the movement.
     * @param movementDirection the movement direction.
     * @param numberOfMovements the number of steps in the movement. Each step has the speed of {@code MOVEMENT_VELOCITY}.
     * @return the expected final position.
     */
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

    @Test
    void testDoesNotMoveInitially() {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_COORDINATES, MOVEMENT_TEST_MAP);
        assertEquals(initialPosition, movement.move());
        assertEquals(initialPosition, movement.move());
    }

    @Test
    void testDoesNotMoveWithNoneDirection() {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_COORDINATES, MOVEMENT_TEST_MAP);
        movement.changeDirection(NONE);
        assertEquals(initialPosition, movement.move());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testSpecificDirectionMovement(Direction movementDirection) {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_COORDINATES, MOVEMENT_TEST_MAP);
        movement.changeDirection(movementDirection);
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 1), movement.move());
    }

    private void testDirectionChangeWithoutMoving(
            Direction initialMovementDirection,
            UnaryOperator<Direction> directionChanger) {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_COORDINATES, MOVEMENT_TEST_MAP);
        movement.changeDirection(initialMovementDirection);
        movement.changeDirection(directionChanger.apply(initialMovementDirection));
        assertEquals(getExpectedFinalPosition(initialPosition, directionChanger.apply(initialMovementDirection), 1), movement.move());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testOppositeDirectionChangeWithoutMoving(Direction initialMovementDirection) {
        testDirectionChangeWithoutMoving(initialMovementDirection, Direction::getOpposite);
    }

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testPerpendicularDirectionChangeWithoutMoving(Direction initialMovementDirection) {
        testDirectionChangeWithoutMoving(initialMovementDirection, this::getRightTurn);
    }

    @ParameterizedTest
    @CsvSource ({
            "UP, 5, 3",
            "DOWN, 1, 3",
            "LEFT, 3, 7",
            "RIGHT, 3, 1"
    })
    void testOppositeDirectionChangeWithoutMovingWithWall(Direction initialMovementDirection, int initialRow, int initialColumn) {
        Vector2D initialPosition = initializeMovement(new MatrixCoordinates(initialRow, initialColumn), MOVEMENT_TEST_MAP);
        movement.changeDirection(initialMovementDirection);
        movement.changeDirection(initialMovementDirection.getOpposite());
        assertEquals(initialPosition, movement.move());
    }

    @ParameterizedTest
    @CsvSource ({
            "UP, 5, 5",
            "DOWN, 1, 1",
            "LEFT, 1, 5",
            "RIGHT, 5, 1"
    })
    void testPerpendicularDirectionChangeWithoutMovingWithWall(Direction initialMovementDirection, int initialRow, int initialColumn) {
        Vector2D initialPosition = initializeMovement(new MatrixCoordinates(initialRow, initialColumn), MOVEMENT_TEST_MAP);
        movement.changeDirection(initialMovementDirection);
        movement.changeDirection(getRightTurn(initialMovementDirection));
        assertEquals(getExpectedFinalPosition(initialPosition, initialMovementDirection, 1), movement.move());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testCannotMove(Direction movementDirection) {
        Vector2D initialPosition = initializeMovement(new MatrixCoordinates(5, 7), MOVEMENT_TEST_MAP);
        movement.changeDirection(movementDirection);
        assertEquals(initialPosition, movement.move());
    }

    /**
     * Performs multiple movement operations ({@code move()} method) on the MovementManager, ignoring the returned
     * position value of each call.
     * @param numberOfMovements the number of movements operations that must be performed.
     */
    private void moveMultipleTimes(int numberOfMovements) {
        IntStream.range(0, numberOfMovements).forEach((_) -> movement.move());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testMultipleTilesMovement(Direction movementDirection) {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_COORDINATES, MOVEMENT_TEST_MAP);
        movement.changeDirection(movementDirection);
        int numberOfMovements = 2 * MOVEMENTS_IN_A_TILE;
        moveMultipleTimes(numberOfMovements - 1);
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, numberOfMovements), movement.move());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MOVEMENT + "all_directions.csv")
    void testInvertDirection(Direction movementDirection) {
        Vector2D initialPosition = initializeMovement(INITIAL_MOVEMENT_COORDINATES, MOVEMENT_TEST_MAP);
        movement.changeDirection(movementDirection);
        movement.move();
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 2), movement.move());
        assertEquals(INITIAL_MOVEMENT_COORDINATES, movement.currentMatrixCoordinates());
        movement.changeDirection(movementDirection.getOpposite());
        moveMultipleTimes(3);
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection.getOpposite(), 2), movement.move());
        assertEquals(INITIAL_MOVEMENT_COORDINATES, movement.currentMatrixCoordinates());
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
        MatrixCoordinates initialCoordinates = new MatrixCoordinates(startingRow, startingColumn);
        Vector2D initialPosition = initializeMovement(initialCoordinates, MOVEMENT_TEST_MAP);
        movement.changeDirection(movementDirection);
        movement.move();
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 2), movement.move());
        assertEquals(initialCoordinates, movement.currentMatrixCoordinates());
        movement.changeDirection(movementDirection.getOpposite());
        movement.move();
        assertEquals(initialPosition, movement.move());
        assertEquals(initialPosition, movement.move());
        assertEquals(initialPosition, movement.move());
        assertEquals(initialCoordinates, movement.currentMatrixCoordinates());
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
        MatrixCoordinates initialCoordinates = new MatrixCoordinates(startingRow, startingColumn);
        Vector2D initialPosition = initializeMovement(new MatrixCoordinates(startingRow, startingColumn), MOVEMENT_TEST_MAP);
        movement.changeDirection(movementDirection);
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 1), movement.move());
        movement.changeDirection(getRightTurn(movementDirection));
        // Assert that it keeps moving in the same direction as before, despite specifying a different one
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, 2), movement.move());
        assertEquals(initialCoordinates, movement.currentMatrixCoordinates());
        moveMultipleTimes(MOVEMENTS_IN_A_TILE - 3);
        Vector2D turningPosition = movement.move();
        MatrixCoordinates turningCoordinates = initialCoordinates.getNeighbour(movementDirection, map.getGridSize());
        assertEquals(getExpectedFinalPosition(initialPosition, movementDirection, MOVEMENTS_IN_A_TILE), turningPosition);
        assertEquals(turningCoordinates, movement.currentMatrixCoordinates());
        moveMultipleTimes(MOVEMENTS_IN_A_TILE - 1);
        // Assert that it starts moving in the desired direction, after reaching the junction
        assertEquals(getExpectedFinalPosition(turningPosition, getRightTurn(movementDirection), MOVEMENTS_IN_A_TILE), movement.move());
        movement.changeDirection(movementDirection);
        moveMultipleTimes(MOVEMENTS_IN_A_TILE - 1);
        Vector2D secondTurningPosition = movement.move();
        MatrixCoordinates secondTurningCoordinates = turningCoordinates
                .getNeighbour(getRightTurn(movementDirection), map.getGridSize())
                .getNeighbour(getRightTurn(movementDirection), map.getGridSize());
        // Assert that it keeps moving in the same direction as before, despite specifying a different one
        assertEquals(getExpectedFinalPosition(turningPosition, getRightTurn(movementDirection), 2 * MOVEMENTS_IN_A_TILE), secondTurningPosition);
        assertEquals(secondTurningCoordinates, movement.currentMatrixCoordinates());
        // Assert that the second turn was made
        assertEquals(getExpectedFinalPosition(secondTurningPosition, movementDirection, 1), movement.move());
    }

    @ParameterizedTest
    @CsvSource({
            "UP, 0, 1, 2, 1",
            "DOWN, 2, 1, 0, 1",
            "LEFT, 1, 0, 1, 2",
            "RIGHT, 1, 2, 1, 0"
    })
    void testToroid(Direction movementDirection, int initialRow, int initialColumn, int finalRow, int finalColumn) {
        initializeMovement(new MatrixCoordinates(initialRow, initialColumn), TOROID_TEST_MAP);
        movement.changeDirection(movementDirection);
        Vector2D finalPosition = map.getTile(new MatrixCoordinates(finalRow, finalColumn)).getCenterPosition();
        moveMultipleTimes(MOVEMENTS_IN_A_TILE - 1);
        assertEquals(finalPosition, movement.move());
    }

    @ParameterizedTest
    @CsvSource({
            "UP, 0, 2",
            "DOWN, 2, 1",
            "LEFT, 2, 0",
            "RIGHT, 1, 2"
    })
    void testToroidWithWalls(Direction movementDirection, int initialRow, int initialColumn) {
        Vector2D initialPosition = initializeMovement(new MatrixCoordinates(initialRow, initialColumn), TOROID_WITH_WALLS_TEST_MAP);
        movement.changeDirection(movementDirection);
        assertEquals(initialPosition, movement.move());
    }

    // TODO: test currentMatrixCoordinates()
}
