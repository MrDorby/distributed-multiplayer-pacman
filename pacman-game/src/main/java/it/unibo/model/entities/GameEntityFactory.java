package it.unibo.model.entities;

import it.unibo.model.map.Tile;

public interface GameEntityFactory {
    Pacman createPacman(Tile spawnPoint);
    Dot createDot(Tile tile);
    Ghost createGhost();
    GameEntity unmodifiableGameEntity(GameEntity entity);
}
