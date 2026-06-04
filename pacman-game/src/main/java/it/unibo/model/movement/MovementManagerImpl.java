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
    private Direction movementDirection;

    public MovementManagerImpl(final GameMap map, final MatrixCoordinates initialMatrixPosition, final int velocity) {
        this.velocity = velocity;
        this.map = map;
        this.targetMatrixPosition = initialMatrixPosition;
        this.position = this.map.getTile(initialMatrixPosition).getCenterPosition();
        this.movementDirection = Direction.NONE;
    }

    private Vector2D calculateNewPosition() {
        return switch (this.movementDirection) {
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
        return this.position;
    }

    private Tile getTile(MatrixCoordinates tileCoordinates) {
        return this.map.getTile(tileCoordinates);
    }

    private boolean isCurrentPositionInTileCenter(MatrixCoordinates tileCoordinates) {
        return this.position.equals(getTile(tileCoordinates).getCenterPosition());
    }

    @Override
    public void changeDirection(Direction direction) {
        if (isCurrentPositionInTileCenter(this.targetMatrixPosition)) {
            if (getTile(this.targetMatrixPosition.getNeighbour(direction)).getTileType() != TileType.WALL) {
                this.movementDirection = direction;
                this.targetMatrixPosition = this.targetMatrixPosition.getNeighbour(direction);
            } else {
                this.movementDirection = Direction.NONE;
            }
        } else {
            if (direction.equals(this.movementDirection.getOpposite())) {
                // TODO: handle the case in which we change direction while still being in the center of the starting
                //  point (example: we call changeDirection(RIGHT) and then changeDirection(LEFT).
                this.movementDirection = direction;
                this.targetMatrixPosition = this.targetMatrixPosition.getNeighbour(direction);
            }
            // TODO: handle the case in which the direction is perpendicular (or the same), by memorizing the desired
            //  movement direction
        }
    }
}
