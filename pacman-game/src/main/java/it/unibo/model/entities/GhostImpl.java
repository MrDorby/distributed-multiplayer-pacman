package it.unibo.model.entities;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Direction;
import it.unibo.model.common.GameConstants;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;
import it.unibo.model.movement.MovementManager;
import it.unibo.model.movement.MovementManagerImpl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GhostImpl extends GameEntityImpl implements Ghost {

    private final static int TIME_TO_RESPAWN = 5000;  // 5000 millis
    private final static int TIME_TO_CHANGE_DIRECTION = 500;
    private final static int GHOST_VALUE = 1;
    private MovementManager movementManager;
    private long lastTimeDead;

    @Override
    public long getLastTimeDirectionChanged() {
        return lastTimeDirection;
    }

    @Override
    public long getLastTimeDead() {
        return lastTimeDead;
    }

    private long lastTimeDirection;

    public GhostImpl(Tile tile, GameMap map) {
        super(tile);
        setMovementStart(tile, map);
    }

    public GhostImpl(Tile tile, GameMap map, boolean isAlive, long lastTimeDead, long lastTimeDirection) {
        super(tile);
        setMovementStart(tile, map);
        this.lastTimeDead = lastTimeDead;
        this.lastTimeDirection = lastTimeDirection;
        if (!isAlive) this.setIsAlive(false);
    }

    private void setMovementStart(Tile tile, GameMap map) {
        this.movementManager = new MovementManagerImpl(
                map,
                tile.getMatrixPosition(),
                GameConstants.GameEntityFeatures.GHOST.getVelocity()
        );
        this.movementManager.changeDirection(MovableEntity.getRandomDirection());
    }

    @Override
    public void update(GameContext currentContext) {
        Set<Collision> collision = currentContext.getCollisions(this);
        Stream<Pacman> pacman = collision.stream()
                .filter(x -> x.getGameEntity() instanceof Pacman)
                .map(x -> (Pacman) x.getGameEntity());
        pacman.findFirst().ifPresent(x -> checkGhostCanBeEaten(x, currentContext));
        if (!this.isAlive()) {
            long timeLeft = currentContext.getGameState().getTimeLeftInMillis();
            if (this.lastTimeDead - timeLeft >= TIME_TO_RESPAWN) {
                this.setIsAlive(true);
            }
        } else {
            Vector2D nextPosition = this.movementManager.move();
            long timeLeft = currentContext.getGameState().getTimeLeftInMillis();
            if (this.lastTimeDirection - timeLeft >= TIME_TO_CHANGE_DIRECTION || getPosition().equals(nextPosition)) {
                Direction direction = getRandomAvailableDirection(currentContext.getMap());
                this.movementManager.changeDirection(direction);
                this.lastTimeDirection = currentContext.getGameState().getTimeLeftInMillis();
            }
            setPosition(nextPosition);
        }
    }

    /* Gets one of the available directions to move for the ghost. */
    private Direction getRandomAvailableDirection(GameMap map) {
        MatrixCoordinates coordinates = this.movementManager.currentMatrixCoordinates();
        List<Direction> supp = MovableEntity.getWalkableDirection(coordinates, map)
            .values().stream().toList();
        return supp.stream()
                    .filter(x -> x != this.movementManager.getCurrentDirection().getOpposite())
                    .collect(Collectors
                        .collectingAndThen(
                            Collectors.toList(), 
                            collected -> {
                                Collections.shuffle(collected);
                                return collected.stream();
                        }
                )).findFirst().get();
    }

    /* Checks if the ghost is alive or not by means of the pacman that collided with the ghost,
    and setting the new position to the ghost spwan point. */
    private void checkGhostCanBeEaten(Pacman pacman, GameContext context) {
        if (pacman.canEatGhost()) {
            this.setIsAlive(false);
            super.setPosition(context.getMap().getGhostSpawnPoint().getCenterPosition());
            setMovementStart(context.getMap().getGhostSpawnPoint(), context.getMap());
            this.lastTimeDead = context.getGameState().getTimeLeftInMillis();
        }
    }

    @Override
    public int getGhostValue() {
        return GHOST_VALUE;
    }

    @Override
    public MatrixCoordinates getMatrixCoordinates() {
        return this.movementManager.currentMatrixCoordinates();
    }

    @Override
    public Direction getDirection() {
        return this.movementManager.getCurrentDirection();
    }
}
