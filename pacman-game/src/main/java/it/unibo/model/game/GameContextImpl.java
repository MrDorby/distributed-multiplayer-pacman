package it.unibo.model.game;

import it.unibo.model.collisions.Collision;
import it.unibo.model.entities.*;
import it.unibo.model.map.Map;

import java.util.HashSet;
import java.util.Set;

public class GameContextImpl implements GameContext {
    private Map map;
    private Set<Dot> dots;
    private Set<Ghost> ghosts;
    private Set<Pacman> pacmans;
    private GameState gameState;
    private Set<Collision> collisions;

    @Override
    public Set<Collision> getCollisions(GameEntity entity) {
        return Set.of();
    }

    @Override
    public Set<GameEntity> getGameEntities() {
        Set<GameEntity> gameEntities = new HashSet<>();
        gameEntities.addAll(dots);
        gameEntities.addAll(ghosts);
        gameEntities.addAll(pacmans);
        return gameEntities;
    }

    @Override
    public Set<MovableEntity> getMovableEntities() {
        Set<MovableEntity> movableEntities = new HashSet<>();
        movableEntities.addAll(ghosts);
        movableEntities.addAll(pacmans);
        return movableEntities;
    }

    public Map getMap() {
        return this.map;
    }

    public Set<Dot> getDots() {
        return this.dots;
    }

    public Set<Ghost> getGhosts() {
        return this.ghosts;
    }

    public Set<Pacman> getPacmans() {
        return this.pacmans;
    }

    @Override
    public GameState getGameState() {
        return this.gameState;
    }
}
