package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;

import java.util.Optional;

public record TileImpl(
        MatrixCoordinates matrixPosition,
        Vector2D centerPosition,
        Optional<Dot> dot,
        TileType type
) implements Tile {

    public TileImpl {
        if (type != TileType.SIMPLE && dot.isPresent()) {
            throw new IllegalArgumentException("A non-simple Tile may not contain a Dot.");
        }
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
    public MatrixCoordinates getMatrixPosition() {
        return this.matrixPosition;
    }

    @Override
    public TileType getTileType() {
        return this.type;
    }
}
