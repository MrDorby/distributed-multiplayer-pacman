package it.unibo.model.entities;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Direction;
import it.unibo.model.common.GameConstants;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;
import it.unibo.model.map.TileType;
import it.unibo.model.movement.MovementManager;
import it.unibo.model.movement.MovementManagerImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PacmanImpl extends GameEntityImpl implements Pacman {
    private final static int NUMBER_LIVES = 3;
    private final static int ABILITY_TO_EAT_GHOSTS_DURATION_IN_MILLIS = 3000;
    private final static int INVINCIBILITY_DURATION_IN_MILLIS = 2000;
    private static final int DIRECTION_CHANGE_COOLDOWN_MILLIS = 500;
    protected final MovementManager movementManager;
    private String id;
    private int score;
    private int lives;
    private boolean controlledByPlayer;
    private boolean canEatGhosts;
    private boolean invincible;
    private long lastTimeSpecialDotWasEaten;
    private long lastTimeBecameInvincible;
    private long lastTimeDirectionWasChanged;

    public PacmanImpl(Tile tile, GameMap map) {
        super(tile);
        this.lives = NUMBER_LIVES;
        this.controlledByPlayer = true;
        this.movementManager = new MovementManagerImpl(map, 
            tile.getMatrixPosition(), 
            GameConstants.GameEntityFeatures.PACMAN.getVelocity());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) throws IllegalArgumentException {
        if (this.id == null) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID is already set!");
        }
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public int getLives() {
        return this.lives;
    }

    @Override
    public void move(Direction direction) {
        this.movementManager.changeDirection(direction);
    }

    @Override
    public boolean isPlayer() {
        return this.controlledByPlayer;
    }

    @Override
    public boolean canEatGhost() {
        return this.canEatGhosts;
    }

    @Override
    public void changeBehaviour(boolean isPlayer) {
        this.controlledByPlayer = isPlayer;
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
    public boolean isAlive() {
        return this.lives > 0;
    }

    @Override
    public boolean isInvincible() {
        return invincible;
    }

    @Override
    public void update(GameContext currentContext) {
        if (this.lastTimeDirectionWasChanged == 0) {
            this.lastTimeDirectionWasChanged = currentContext.getGameState().getTimeLeftInMillis();
        }
        // Collision checking should be performed only when the pacman is alive.
        if (this.isAlive()) {
            checkCollisions(currentContext);
        }
        if (this.isAlive()) {
            checkStateExpirations(currentContext);
            if (!controlledByPlayer) {
                handleBotBehaviour(currentContext);
            }
            super.setPosition(this.movementManager.move());
        }
    }

    private void checkCollisions(GameContext context) {
        Set<Collision >collisions = context.getCollisions(this);
        // Check for Ghost interactions
        collisions.stream()
                .filter(collision -> collision.getInvolvedEntity() instanceof Ghost)
                .map(collision -> (Ghost) collision.getInvolvedEntity())
                .findFirst()
                .ifPresent(ghost -> checkGhostCollision(ghost, context));
        // Check for Dot interactions only if still alive after checking ghosts
        if (this.isAlive()) {
            collisions.stream()
                    .filter(collision -> collision.getInvolvedEntity() instanceof Dot)
                    .map(collision -> (Dot) collision.getInvolvedEntity())
                    .findFirst()
                    .ifPresent(dot -> checkDotCollision(dot, context));
        }
    }

    private void checkGhostCollision(Ghost ghost, GameContext context) {
        if (!this.canEatGhosts && !this.invincible) {
            if (this.lives > 0) {
                this.lives = this.lives - 1;
                this.invincible = true;
                this.lastTimeBecameInvincible = context.getGameState().getTimeLeftInMillis();
                // Code below doesn't work. In any case, it's better if it doesn't teleport to
                // a non-deterministic spawn point for speculative execution purposes.
                // List<Tile> tiles = context.getMap().getPacmanSpawnPoints().stream().toList();
                // super.setPosition(tiles.get(new Random().nextInt(0, tiles.size())).getCenterPosition());
            } else {
                super.setIsAlive(false);
            }
        } else if (this.canEatGhosts) {
            this.score = this.score + ghost.getGhostValue();
        }
    }

    /* Checks if the collision made with a dot and if the dot is special. */
    private void checkDotCollision(Dot dot, GameContext context) {
        if (dot.isSpecial()) {
            this.canEatGhosts = true;
            this.lastTimeSpecialDotWasEaten = context.getGameState().getTimeLeftInMillis();
        }
        this.score = this.score + dot.dotValue();
    }

    protected void checkStateExpirations(GameContext context) {
        long currentTime = context.getGameState().getTimeLeftInMillis();
        if (this.invincible && currentTime <= this.lastTimeBecameInvincible - INVINCIBILITY_DURATION_IN_MILLIS) {
            this.invincible = false;
        }
        if (this.canEatGhosts && currentTime <= this.lastTimeSpecialDotWasEaten - ABILITY_TO_EAT_GHOSTS_DURATION_IN_MILLIS) {
            this.canEatGhosts = false;
        }
    }

    /* Chooses how to move the pacman if no user commands it,
    by checking its neighbours or otherwise gets random direction. */
    private void handleBotBehaviour(GameContext context) {
        long currentTime = context.getGameState().getTimeLeftInMillis();
        if (currentTime <= this.lastTimeDirectionWasChanged - DIRECTION_CHANGE_COOLDOWN_MILLIS) {
            GameMap map = context.getMap();
            Map<MatrixCoordinates, Direction> md = MovableEntity
                    .getWalkableDirection(this.movementManager.currentMatrixCoordinates(), map);
            List<Tile> tiles = md.keySet()
                    .stream()
                    .map(map::getTile)
                    .filter(y -> y.getTileType() == TileType.DOT || y.getTileType() == TileType.SPECIAL_DOT)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                        Collections.shuffle(collected);return collected;
                    }));
            if (tiles.isEmpty()) {
                this.movementManager.changeDirection(MovableEntity.getRandomDirection());
            } else {
                this.movementManager.changeDirection(md.get(tiles.getFirst().getMatrixPosition()));
            }
            this.lastTimeDirectionWasChanged = context.getGameState().getTimeLeftInMillis();
        }
    }
}
