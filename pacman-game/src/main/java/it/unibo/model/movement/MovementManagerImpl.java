package it.unibo.model.movement;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.map.GameMap;

import static it.unibo.model.map.TileType.WALL;

public class MovementManagerImpl implements MovementManager {
    private final int velocity;
    private final GameMap map;
    private MatrixCoordinates currentMatrixPosition;
    private MatrixCoordinates targetMatrixPosition;
    private Vector2D position;
    private Direction movementDirection;

    public MovementManagerImpl(final GameMap map, final MatrixCoordinates initialMatrixPosition, final int velocity) {
        this.velocity = velocity;
        this.map = map;
        this.targetMatrixPosition = initialMatrixPosition;
        this.currentMatrixPosition = initialMatrixPosition;
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

    @Override
    public void changeDirection(Direction direction) {
//        if (this.position.equals(this.map.getTile(this.targetMatrixPosition).getCenterPosition())) {
//            // TODO: implement
//        } else {
//            // TODO: immplement
//        }
        this.movementDirection = direction;
    }
}
