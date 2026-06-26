package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;

public record TileImpl(
        MatrixCoordinates matrixPosition,
        Vector2D centerPosition,
        TileType type
) implements Tile {
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
