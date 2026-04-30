package it.unibo.model.map;

import it.unibo.model.common.Vector2D;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameMapImpl implements GameMap {

    private final Map<Vector2D, Tile> tilesGrid;
    // TODO: consider creating a set of Tiles containing the Pacman spawn points
    //  and a single Tile field containing the ghosts' spawn point.
    //  Initialize these structures in the constructor.

    public GameMapImpl(Map<Vector2D, Tile> tilesGrid) {
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
    public Tile getTileFromMatrixPosition(Vector2D matrixPosition) {
        return null;
    }
}
