package it.unibo.model.map;

import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;

import java.util.Optional;

public class TileImpl implements Tile {

    private final Vector2D matrixPosition;
    private final Vector2D centerPosition;
    private final Optional<Dot> dot;
    private final TileType tileType;

    // TODO: Understand what is necessary for the constructor.
    public TileImpl(Vector2D matrixPosition,
                    Vector2D centerPosition,
                    Optional<Dot> dot,
                    TileType tileType) {
        this.matrixPosition = matrixPosition;
        this.centerPosition = centerPosition;
        this.dot = dot;
        this.tileType = tileType;
    }

    @Override
    public boolean isWall() {
        return this.tileType == TileType.WALL;
    }

    @Override
    public Optional<Dot> getDot() {
        return this.dot;
    }

    @Override
    public Vector2D getCenterPosition() {
        return this.centerPosition;
    }

    @Override
    public Vector2D getMatrixPosition() {
        return this.matrixPosition;
    }

    @Override
    public TileType getTileType() {
        return this.tileType;
    }
}
