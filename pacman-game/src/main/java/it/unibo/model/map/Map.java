package it.unibo.model.map;

import java.util.Set;

public interface Map {
    Set<Tile> getPacmanSpawnPoints();
    Tile getGhostSpawnPoint();
}
