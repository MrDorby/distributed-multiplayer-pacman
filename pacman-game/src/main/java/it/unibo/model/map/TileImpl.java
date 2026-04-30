package it.unibo.model.map;

import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;

import java.util.Optional;

public record TileImpl(
        Vector2D matrixPosition,
        Vector2D centerPosition,
        Vector2D size, // TODO: keep or use fixed size?
        Optional<Dot> dot,
        TileType type
) implements Tile {

    public TileImpl {
        if (type != TileType.SIMPLE && dot.isPresent()) {
            throw new IllegalArgumentException("A non-simple Tile may not contain a Dot.");
        }
    }

    @Override
    public boolean isWall() {
        return this.type == TileType.WALL;
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
        return this.type;
    }
}
