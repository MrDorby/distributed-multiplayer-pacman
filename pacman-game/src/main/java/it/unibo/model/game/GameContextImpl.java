package it.unibo.model.game;

import it.unibo.model.collisions.Collision;
import it.unibo.model.entities.*;
import it.unibo.model.map.GameMap;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class GameContextImpl implements GameContext {
    private final GameMap map;
    private final Set<Dot> dots;
    private final Set<Ghost> ghosts;
    private final Set<Pacman> pacmans;
    private GameState gameState;
    private Duration timeLeft;
    private Map<GameEntity, Set<Collision>> collisions = new HashMap<>();

    public GameContextImpl(GameMap map, Set<Dot> dots, Set<Ghost> ghosts, Set<Pacman> pacmans, Duration timeLeft) {
        this.map = map;
        this.dots = dots;
        this.ghosts = ghosts;
        this.pacmans = pacmans;
        this.timeLeft = timeLeft;
        this.createGameState();
    }

    @Override
    public Set<Collision> getCollisions(GameEntity entity) {
        return collisions.getOrDefault(entity, Collections.emptySet());
    }

    @Override
    public void setCollisions(Map<GameEntity, Set<Collision>> collisions) {
        this.collisions = collisions;
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

    @Override
    public GameMap getMap() {
        return this.map;
    }

    @Override
    public Set<Dot> getDots() {
        return this.dots;
    }

    @Override
    public Set<Ghost> getGhosts() {
        return this.ghosts;
    }

    @Override
    public Set<Pacman> getPacmans() {
        return this.pacmans;
    }

    @Override
    public GameState getGameState() {
        return this.gameState;
    }

    @Override
    public void decrementTime(Duration delta) {
        this.timeLeft = this.timeLeft.minus(delta);
        if (this.timeLeft.isNegative()) {
            this.timeLeft = Duration.ZERO;
        }
    }

    @Override
    public void createGameState() {
        this.gameState = new GameStateImpl(
            calculateLeaderboard(),
            this.timeLeft,
            checkGameOver(),
            calculateWinner()
        );
    }

    private Map<Pacman, Integer> calculateLeaderboard() {
        return pacmans.stream().collect(Collectors.toMap(pacman -> pacman, Pacman::getScore));
    }

    private boolean checkGameOver() {
        boolean timeIsUp = timeLeft.isZero() || timeLeft.isNegative();
        boolean onlyOnePacmanLeft = pacmans.stream().filter(pacman -> pacman.getLives() > 0).count() == 1;
        return timeIsUp || onlyOnePacmanLeft;
    }

    private Pacman calculateWinner() {
        if (checkGameOver()) {
            return pacmans.stream().filter(pacman -> pacman.getLives() > 0).max(Comparator.comparingInt(Pacman::getScore)).orElse(null);
        }
        return null;
    }
}
