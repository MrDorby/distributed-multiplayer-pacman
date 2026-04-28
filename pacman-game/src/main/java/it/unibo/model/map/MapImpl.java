package it.unibo.model.map;

import java.util.Set;
import java.util.stream.Collectors;

public class MapImpl implements Map {

    private final Set<Tile> tiles;

    public MapImpl(Set<Tile> tiles) {
        this.tiles = tiles;
    }

    @Override
    public Set<Tile> getTiles() {
        return this.tiles;
    }

    @Override
    public Set<Tile> getPacmanSpawnPoints() {
        return tiles.stream()
                .filter(x -> x.getTileType() == TileType.PACMAN_SPAWN)
                .collect(Collectors.toSet());
    }

    // TODO: Can it be reasonable?
    @Override
    public Tile getGhostSpawnPoint() {
        return tiles.stream()
                .filter(x -> x.getTileType() == TileType.GHOST_SPAWN)
                .findFirst().get();
    }
}
