package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

/**
 * A factory that instantiates speculative and non-authoritative versions of game entities
 * designed specifically for client-side execution.
 */
public class SpeculativeEntityFactoryImpl implements GameEntityFactory {

    @Override
    public Pacman createPacman(Tile spawnPoint, GameMap map) {
        return new SpeculativePacman(spawnPoint, map);
    }

    @Override
    public Dot createDot(Vector2D position, boolean isSpecial) {
        return new SpeculativeDot(position, isSpecial);
    }

    @Override
    public Ghost createGhost(Tile spawnPoint, GameMap map) {
        return new SpeculativeGhost(spawnPoint, map);
    }

    @Override
    public GameEntity unmodifiableGameEntity(GameEntity entity) {
        return new GameEntityProxy(entity);
    }
}
