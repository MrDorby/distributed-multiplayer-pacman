package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

public class GameEntityFactoryImpl implements GameEntityFactory {

    @Override
    public Pacman createPacman(Tile spawnPoint, GameMap map) {
        return new PacmanImpl(spawnPoint, map);
    }

    @Override
    public Dot createDot(Vector2D position, boolean isSpecial) {
        Dot dot = new DotImpl(position);
        dot.setIsSpecial(isSpecial);
        return dot;
    }

    @Override
    public Ghost createGhost(Vector2D spawnPoint, GameMap map) {
        return new GhostImpl(spawnPoint, map);
    }

    @Override
    public GameEntity unmodifiableGameEntity(GameEntity entity) {
        return new GameEntityProxy(entity);
    }
}
