package it.unibo.model.game;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.entities.*;
import it.unibo.model.map.GameMap;

import java.util.*;
import java.util.stream.Collectors;

public class GameContextImpl implements GameContext {
    private final GameMap map;
    private final Map<MatrixCoordinates, Dot> dotsMap;
    private final Set<Ghost> ghosts;
    private final Set<Pacman> pacmans;
    private GameState gameState;
    private long timeLeftInMillis;
    private Map<GameEntity, Set<Collision>> collisions = new HashMap<>();

    public GameContextImpl(GameMap map, Map<MatrixCoordinates, Dot> dotsMap, Set<Ghost> ghosts, Set<Pacman> pacmans, long timeLeftInMillis) {
        this.map = map;
        this.dotsMap = dotsMap;
        this.ghosts = ghosts;
        this.pacmans = pacmans;
        this.timeLeftInMillis = timeLeftInMillis;
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
        gameEntities.addAll(dotsMap.values());
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
    public Map<MatrixCoordinates, Dot> getDotsMap() {
        return this.dotsMap;
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
    public void decrementTime(long deltaInMillis) {
        this.timeLeftInMillis = this.timeLeftInMillis - deltaInMillis;
        if (this.timeLeftInMillis < 0) {
            this.timeLeftInMillis = 0;
        }
    }

    @Override
    public void createGameState() {
        this.gameState = new GameStateImpl(
            calculateLeaderboard(),
            this.timeLeftInMillis,
            checkGameOver(),
            findWinnerId()
        );
    }

    private Map<String, Integer> calculateLeaderboard() {
        return pacmans.stream()
                .filter(pacman -> pacman.getId() != null)
                .sorted(Comparator.comparing(Pacman::getScore).reversed())
                .collect(Collectors.toMap(
                        Pacman::getId,
                        Pacman::getScore,
                        (a, _) -> a,
                        LinkedHashMap::new));
    }

    private boolean checkGameOver() {
        boolean timeIsUp = timeLeftInMillis <= 0;
        boolean onlyOnePacmanLeft = pacmans.stream().filter(pacman -> pacman.getLives() > 0).count() == 1;
        return timeIsUp || onlyOnePacmanLeft;
    }

    private String findWinnerId() {
        if (checkGameOver()) {
            return pacmans.stream()
                    .filter(pacman -> pacman.getLives() > 0)
                    .max(Comparator.comparingInt(Pacman::getScore))
                    .map(Pacman::getId).orElse(null);
        }
        return null;
    }
}
