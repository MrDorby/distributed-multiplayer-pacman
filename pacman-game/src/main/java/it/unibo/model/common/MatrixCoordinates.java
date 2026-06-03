package it.unibo.model.common;

public record MatrixCoordinates(int row, int column) {
    // TODO: implement toroidal neighbour
    public MatrixCoordinates getNeighbour(Direction direction) {
        return switch (direction) {
            case UP -> new MatrixCoordinates(this.row - 1, this.column);
            case DOWN -> new MatrixCoordinates(this.row + 1, this.column);
            case LEFT -> new MatrixCoordinates(this.row, this.column - 1);
            case RIGHT -> new MatrixCoordinates(this.row, this.column + 1);
            case NONE -> this;
        };
    }
}
