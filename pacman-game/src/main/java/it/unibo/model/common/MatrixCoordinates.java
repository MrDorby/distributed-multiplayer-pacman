package it.unibo.model.common;

public record MatrixCoordinates(int row, int column) {
    /**
     * Computes the neighbouring coordinates in the grid, based on the specified direction. In case these coordinates
     * are in the border of the grid, the returned neighbour is calculated as in a toroid. (index {@code gridSize} becomes {@code 0}).
     * @param direction the specified direction.
     * @param gridSize the size of the coordinates grid.
     * @return the neighbouring coordinates.
     */
    public MatrixCoordinates getNeighbour(Direction direction, MatrixCoordinates gridSize) {
        return switch (direction) {
            case UP -> new MatrixCoordinates((gridSize.row() + (this.row - 1)) % gridSize.row(), this.column);
            case DOWN -> new MatrixCoordinates((this.row + 1) % gridSize.row(), this.column);
            case LEFT -> new MatrixCoordinates(this.row, (gridSize.column() + (this.column - 1)) % gridSize.column());
            case RIGHT -> new MatrixCoordinates(this.row, (this.column + 1) % gridSize.column());
            case NONE -> this;
        };
    }
}
