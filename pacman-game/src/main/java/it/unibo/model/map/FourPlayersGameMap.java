package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinate;
import it.unibo.model.common.Vector2D;

import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: write documentation about this map's constraints
public class FourPlayersGameMap implements GameMap {

    private final Map<MatrixCoordinate, Tile> tilesGrid;
    // TODO: consider creating a set of Tiles containing the Pacman spawn points
    //  and a single Tile field containing the ghosts' spawn point.
    //  Initialize these structures in the constructor.

    public FourPlayersGameMap(Map<MatrixCoordinate, Tile> tilesGrid, MatrixCoordinate gridSize) {
        this.tilesGrid = tilesGrid;
        // TODO: implement any consistency checks on the given grid.
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
    public Tile getTile(MatrixCoordinate matrixPosition) {
        return null;
    }
}
