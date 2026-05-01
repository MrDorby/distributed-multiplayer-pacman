package it.unibo.model.entities;

import it.unibo.model.map.Tile;

public class GameEntityFactoryImpl implements GameEntityFactory {

    @Override
    public Pacman createPacman(Tile spawnPoint) {
        return new PacmanImpl(spawnPoint);
    }

    @Override
    public Dot createDot(Tile tile) {
        return new DotImpl(tile);
    }

    @Override
    public Ghost createGhost(Tile spawnPoint) {
        return new GhostImpl(spawnPoint);
    }

    @Override
    public GameEntity unmodifiableGameEntity(GameEntity entity) {
        return new GameEntityProxy(entity);
    }
}
