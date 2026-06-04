package it.unibo.model.movement;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;
import it.unibo.model.map.TileType;

public class MovementManagerImpl implements MovementManager {
    private final int velocity;
    private final GameMap map;
    private MatrixCoordinates targetMatrixPosition;
    private Vector2D position;
    private Direction currentDirection;
    private Direction desiredDirection;

    public MovementManagerImpl(final GameMap map, final MatrixCoordinates initialMatrixPosition, final int velocity) {
        this.velocity = velocity;
        this.map = map;
        this.targetMatrixPosition = initialMatrixPosition;
        this.position = this.map.getTile(initialMatrixPosition).getCenterPosition();
        this.currentDirection = Direction.NONE;
        this.desiredDirection = Direction.NONE;
    }

    /**
     * Calculates the new position based on the current direction of movement.
     * @return the new position.
     */
    private Vector2D calculateNewPosition() {
        return switch (this.currentDirection) {
            case UP -> new Vector2D(this.position.x(), this.position.y() - this.velocity);
            case DOWN -> new Vector2D(this.position.x(), this.position.y() + this.velocity);
            case LEFT -> new Vector2D(this.position.x() - this.velocity, this.position.y());
            case RIGHT -> new Vector2D(this.position.x() + this.velocity, this.position.y());
            case NONE -> this.position;
        };
    }

    @Override
    public Vector2D move() {
        this.position = calculateNewPosition();
        if (isCurrentPositionInTileCenter(this.targetMatrixPosition)) {
            if (this.desiredDirection != Direction.NONE && isWalkable(this.targetMatrixPosition.getNeighbour(this.desiredDirection))) {
                setCurrentDirection(this.desiredDirection);
                this.desiredDirection = Direction.NONE;
            } else {
                setCurrentDirection(isWalkable(this.targetMatrixPosition.getNeighbour(this.currentDirection)) ?
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
        this.targetMatrixPosition = this.targetMatrixPosition.getNeighbour(direction);
    }

    /**
     * Returns the previous matrix position of the entity, according to the current target position and movement direction.
     * Such position is the neighbour of the target position, in the opposite direction to the current movement direction.
     * @return the previous matrix position.
     */
    private MatrixCoordinates getPreviousMatrixPosition() {
        return this.targetMatrixPosition.getNeighbour(this.currentDirection.getOpposite());
    }

    @Override
    public void changeDirection(Direction direction) {
        this.desiredDirection = Direction.NONE;
        if (isCurrentPositionInTileCenter(this.targetMatrixPosition)) {
            if (isWalkable(this.targetMatrixPosition.getNeighbour(direction))) {
                setCurrentDirection(direction);
            } else {
                this.desiredDirection = direction;
            }
        } else {
            if (direction.equals(this.currentDirection.getOpposite())) {
                if (isCurrentPositionInTileCenter(getPreviousMatrixPosition())) {
                    this.targetMatrixPosition = getPreviousMatrixPosition();
                    this.currentDirection = Direction.NONE;
                    if (isWalkable(this.targetMatrixPosition.getNeighbour(direction))) {
                        setCurrentDirection(direction);
                    }
                } else {
                    setCurrentDirection(direction);
                }
            } else {
                if (isCurrentPositionInTileCenter(getPreviousMatrixPosition()) && isWalkable(getPreviousMatrixPosition().getNeighbour(direction))) {
                    this.targetMatrixPosition = getPreviousMatrixPosition();
                    setCurrentDirection(direction);
                } else {
                    this.desiredDirection = direction;
                }
            }
        }
    }
}
