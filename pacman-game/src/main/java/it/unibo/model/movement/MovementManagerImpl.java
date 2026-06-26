package it.unibo.model.movement;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;
import it.unibo.model.map.TileType;

/**
 * An implementation of a MovementManager, which also handles toroidal movement.
 */
public class MovementManagerImpl implements MovementManager {
    private final int velocity;
    private final GameMap map;
    private MatrixCoordinates targetMatrixCoordinates;
    private MatrixCoordinates currentMatrixCoordinates;
    private Vector2D position;
    private Direction currentDirection;
    private Direction desiredDirection;

    /**
     * @param map the GameMap in which the entity moves.
     * @param initialMatrixCoordinates the initial coordinates of the entity in the map's grid.
     * @param velocity the movement velocity of the entity.
     */
    public MovementManagerImpl(final GameMap map, final MatrixCoordinates initialMatrixCoordinates, final int velocity) {
        this.velocity = velocity;
        this.map = map;
        this.targetMatrixCoordinates = initialMatrixCoordinates;
        this.currentMatrixCoordinates = initialMatrixCoordinates;
        this.position = this.map.getTile(initialMatrixCoordinates).getCenterPosition();
        this.currentDirection = Direction.NONE;
        this.desiredDirection = Direction.NONE;
    }

    /**
     * Calculates the new position based on the current direction of movement.
     * @return the new position.
     */
    private Vector2D calculateNewPosition() {
        Vector2D mapSize = this.map.getSize();
        return switch (this.currentDirection) {
            case UP -> new Vector2D(this.position.x(), (mapSize.y() + (this.position.y() - this.velocity)) % mapSize.y());
            case DOWN -> new Vector2D(this.position.x(), (this.position.y() + this.velocity) % mapSize.y());
            case LEFT -> new Vector2D((mapSize.x() + (this.position.x() - this.velocity)) % mapSize.x(), this.position.y());
            case RIGHT -> new Vector2D((this.position.x() + this.velocity) % mapSize.x(), this.position.y());
            case NONE -> this.position;
        };
    }

    @Override
    public Vector2D move() {
        this.position = calculateNewPosition();
        if (isCurrentPositionInTileCenter(this.targetMatrixCoordinates)) {
            if (this.desiredDirection != Direction.NONE && isWalkable(this.targetMatrixCoordinates.getNeighbour(this.desiredDirection, this.map.getGridSize()))) {
                this.currentMatrixCoordinates = this.targetMatrixCoordinates;
                setCurrentDirection(this.desiredDirection);
                this.desiredDirection = Direction.NONE;
            } else {
                this.currentMatrixCoordinates = this.targetMatrixCoordinates;
                setCurrentDirection(isWalkable(this.targetMatrixCoordinates.getNeighbour(this.currentDirection, this.map.getGridSize())) ?
                        currentDirection : Direction.NONE);
            }
        }
        return this.position;
    }

    /**
     * Returns the Tile corresponding to the given coordinates in the map.
     * @param tileCoordinates the Tile's coordinates in the map.
     * @return the Tile.
     */
    private Tile getTile(MatrixCoordinates tileCoordinates) {
        return this.map.getTile(tileCoordinates);
    }

    /**
     * Checks whether the current position of the entity corresponds to the center of the Tile at the given coordinates.
     * @param tileCoordinates the coordinates of the Tile.
     * @return true if the current position is in the center of the Tile, false otherwise.
     */
    private boolean isCurrentPositionInTileCenter(MatrixCoordinates tileCoordinates) {
        return this.position.equals(getTile(tileCoordinates).getCenterPosition());
    }

    /**
     * Checks whether the entity can move through the Tile at the given coordinates in the game map. (ex. is not a wall)
     * @param tileCoordinates the coordinates of the Tile.
     * @return true if the Tile is walkable, false otherwise.
     */
    private boolean isWalkable(MatrixCoordinates tileCoordinates) {
        return getTile(tileCoordinates).getTileType() != TileType.WALL;
    }

    /**
     * Modifies the current direction and also modifies the target Tile accordingly.
     * @param direction the desired direction of movement.
     */
    private void setCurrentDirection(Direction direction) {
        this.currentDirection = direction;
        this.targetMatrixCoordinates = this.targetMatrixCoordinates.getNeighbour(direction, this.map.getGridSize());
    }

    /**
     * Returns the previous matrix coordinates of the entity, according to the current target coordinates and movement direction.
     * Previous coordinates are the neighbor of the target coordinates, in the opposite direction to the current movement direction
     * of the entity.
     * @return the previous matrix coordinates.
     */
    private MatrixCoordinates getPreviousMatrixPosition() {
        return this.targetMatrixCoordinates.getNeighbour(this.currentDirection.getOpposite(), this.map.getGridSize());
    }

    @Override
    public void changeDirection(Direction direction) {
        this.desiredDirection = Direction.NONE;
        if (isCurrentPositionInTileCenter(this.targetMatrixCoordinates)) {
            if (isWalkable(this.targetMatrixCoordinates.getNeighbour(direction, this.map.getGridSize()))) {
                setCurrentDirection(direction);
            } else {
                this.desiredDirection = direction;
            }
        } else {
            if (direction.equals(this.currentDirection.getOpposite())) {
                if (isCurrentPositionInTileCenter(getPreviousMatrixPosition())) {
                    this.targetMatrixCoordinates = getPreviousMatrixPosition();
                    this.currentDirection = Direction.NONE;
                    if (isWalkable(this.targetMatrixCoordinates.getNeighbour(direction, this.map.getGridSize()))) {
                        setCurrentDirection(direction);
                    }
                } else {
                    setCurrentDirection(direction);
                }
            } else {
                if (isCurrentPositionInTileCenter(getPreviousMatrixPosition()) && isWalkable(getPreviousMatrixPosition().getNeighbour(direction, this.map.getGridSize()))) {
                    this.targetMatrixCoordinates = getPreviousMatrixPosition();
                    setCurrentDirection(direction);
                } else {
                    this.desiredDirection = direction;
                }
            }
        }
    }

    // TODO: modify this method
    @Override
    public MatrixCoordinates currentMatrixCoordinates() {
        return this.currentMatrixCoordinates;
    }
}
