package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A GameMap that only accepts four-player maps.
 * Valid maps must have exactly one ghost spawn point and four Pacman spawn points.
 */
public class FourPlayersGameMap implements GameMap {

    private static final int PLAYERS_NUM = 4;

    private final Map<MatrixCoordinates, Tile> tilesGrid;
    private final Set<Tile> pacmanSpawnPoints;
    private final Tile ghostsSpawnPoint;

    // TODO: this class could be generalized by specifying the number of ghost and Pacman spawn points.
    //  In that case, the Factory could be turned into a builder that progressively accepts the map's parameters.
    public FourPlayersGameMap(Map<MatrixCoordinates, Tile> tilesGrid, MatrixCoordinates gridSize) {
        IntStream.range(0, gridSize.row()).forEach(i ->
                IntStream.range(0, gridSize.column()).forEach(j -> {
                    MatrixCoordinates coordinates = new MatrixCoordinates(i, j);
                    Tile tile = tilesGrid.get(coordinates);
                    if (tile == null) {
                        throw new IllegalArgumentException("Not all positions in the map matrix have been initialized:" +
                                " Position (" + i + ", " + j + ") is null");
                    }
                    if (!tile.getMatrixPosition().equals(coordinates)) {
                        throw new IllegalArgumentException("Invalid indexing in a map tile: matrix position does not " +
                                "correspond to the one specified in the tile." +
                                "Tile's position: (" + tile.getMatrixPosition().row() + ", " +
                                tile.getMatrixPosition().column() + ")." +
                                " Position in the matrix: (" + coordinates.row() + ", " + coordinates.column() + ").");
                    }
                }));
        this.tilesGrid = tilesGrid;
        this.pacmanSpawnPoints = tilesGrid.values().stream()
                .filter(t -> t.getTileType() == TileType.PACMAN_SPAWN)
                .collect(Collectors.toSet());
        if (this.pacmanSpawnPoints.size() != PLAYERS_NUM) {
            throw new IllegalArgumentException("Wrong number of Pacman spawn points in the given map. " +
                    "There must be exactly " + PLAYERS_NUM + "spawn points.");
        }
        List<Tile> ghostSpawnPoints = tilesGrid.values().stream()
                .filter(t -> t.getTileType() == TileType.GHOST_SPAWN).toList();
        if (ghostSpawnPoints.size() == 1) {
            this.ghostsSpawnPoint = ghostSpawnPoints.getFirst();
        } else {
            throw new IllegalArgumentException("There can only be one ghost spawn point.");
        }
    }

    @Override
    public Set<Tile> getPacmanSpawnPoints() {
//        return tilesGrid.stream()
//                .filter(x -> x.getTileType() == TileType.PACMAN_SPAWN)
//                .collect(Collectors.toSet());
        return null; // TODO: implement
    }

    // TODO: Can it be reasonable?
    @Override
    public Tile getGhostSpawnPoint() {
//        return tilesGrid.stream()
//                .filter(x -> x.getTileType() == TileType.GHOST_SPAWN)
//                .findFirst().get();
        return null; // TODO: implement
    }

    @Override
    public Tile getTile(MatrixCoordinates matrixPosition) {
        return null;
    }
}
