package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.map.Tile;

public class GameEntityFactoryImpl implements GameEntityFactory {

    @Override
    public Pacman createPacman(Tile spawnPoint) {
        return new PacmanImpl(spawnPoint);
    }

    @Override
    public Dot createDot(Vector2D position, boolean isSpecial) {
        Dot dot = new DotImpl(position);
        dot.setIsSpecial(isSpecial);
        return dot;
    }

    @Override
    public Ghost createGhost(Vector2D spawnPoint) {
        return new GhostImpl(spawnPoint);
    }

    @Override
    public GameEntity unmodifiableGameEntity(GameEntity entity) {
        return new GameEntityProxy(entity);
    }
}
