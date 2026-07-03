package it.unibo.model.entities;

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
import java.util.stream.Collectors;

public class GhostImpl extends GameEntityImpl implements Ghost {
    private final static int TIME_TO_RESPAWN_IN_MILLIS = 5000;
    private final static int DIRECTION_CHANGE_COOLDOWN_MILLIS = 500;
    private final static int GHOST_VALUE = 1;
    protected MovementManager movementManager;
    private long lastTimeDead;
    private long lastTimeDirectionWasChanged;

    public GhostImpl(Tile tile, GameMap map) {
        super(tile);
        initializeMovement(tile, map);
    }

    private void initializeMovement(Tile tile, GameMap map) {
        this.movementManager = new MovementManagerImpl(map, tile.getMatrixPosition(), GameConstants.GameEntityFeatures.GHOST.getVelocity());
        this.movementManager.changeDirection(MovableEntity.getRandomDirection());
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

    @Override
    public void update(GameContext currentContext) {
        if (this.isAlive()) {
            checkCollisions(currentContext);
        }
        if (this.isAlive()) {
            handleMovement(currentContext);
        } else {
            handleRespawn(currentContext);
        }
    }

    private void checkCollisions(GameContext currentContext) {
        currentContext.getCollisions(this).stream()
                .filter(collision -> collision.getInvolvedEntity() instanceof Pacman)
                .map(collision -> (Pacman) collision.getInvolvedEntity())
                .filter(Pacman::canEatGhost)
                .findFirst()
                .ifPresent(_ -> processBeingEaten(currentContext));
    }

    private void processBeingEaten(GameContext context) {
        this.setIsAlive(false);
        GameMap map = context.getMap();
        super.setPosition(map.getGhostSpawnPoint().getCenterPosition());
        initializeMovement(map.getGhostSpawnPoint(), map);
        this.lastTimeDead = context.getGameState().getTimeLeftInMillis();
    }

    protected void handleRespawn(GameContext currentContext) {
        long timeLeft = currentContext.getGameState().getTimeLeftInMillis();
        if (this.lastTimeDead - timeLeft >= TIME_TO_RESPAWN_IN_MILLIS) {
            this.setIsAlive(true);
        }
    }

    private void handleMovement(GameContext currentContext) {
        Vector2D nextPosition = this.movementManager.move();
        long timeLeft = currentContext.getGameState().getTimeLeftInMillis();
        if (this.lastTimeDirectionWasChanged - timeLeft >= DIRECTION_CHANGE_COOLDOWN_MILLIS || this.getPosition().equals(nextPosition)) {
            Direction direction = getRandomAvailableDirection(currentContext.getMap());
            this.movementManager.changeDirection(direction);
            this.lastTimeDirectionWasChanged = timeLeft;
        }
        this.setPosition(nextPosition);
    }

    /* Gets one of the available directions to move for the ghost. */
    private Direction getRandomAvailableDirection(GameMap map) {
        MatrixCoordinates coordinates = this.movementManager.currentMatrixCoordinates();
        List<Direction> supp = MovableEntity.getWalkableDirection(coordinates, map).values().stream().toList();
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
}
