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
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PacmanImpl extends GameEntityImpl implements Pacman {

    private final static int NUMBER_LIVES = 3;
    private final static int TIME_CAN_EAT_GHOSTS = 3000;  // 5000 millis
    private final static int TIME_INVINCIBILITY = 2000;
    private static final int TIME_CHANGE_DIRECTION = 500;
    private final MovementManager movementManager;
    private String id;
    private int score;
    private int lives;
    private boolean controlledByPlayer;
    private boolean canEatGhosts;

    @Override
    public boolean isInvincible() {
        return invincible;
    }

    @Override
    public long getWhenInvincible() {
        return whenInvincible;
    }

    private boolean invincible;
    private long whenSpecialDotEat;

    private long whenInvincible;

    @Override
    public long getLastTimeDirectionChanged() {
        return lastTimeDirection;
    }

    private long lastTimeDirection;

    public PacmanImpl(Tile tile, GameMap map) {
        super(tile);
        this.lives = NUMBER_LIVES;
        this.controlledByPlayer = true;
        this.movementManager = new MovementManagerImpl(map, 
            tile.getMatrixPosition(), 
            GameConstants.GameEntityFeatures.PACMAN.getVelocity());
    }

    public PacmanImpl(Tile tile, GameMap map, String id, int score, int lives,
               boolean controlledByPlayer, boolean canEatGhosts,
               boolean invincible, long whenInvincible,
               long whenSpecialDotEat, long lastTimeDirection,
               boolean isAlive) {
        super(tile);
        this.movementManager = new MovementManagerImpl(map,
                tile.getMatrixPosition(),
                GameConstants.GameEntityFeatures.PACMAN.getVelocity());
        this.id = id;
        this.score = score;
        this.lives = lives;
        this.controlledByPlayer = controlledByPlayer;
        this.canEatGhosts = canEatGhosts;
        this.invincible = invincible;
        this.whenInvincible = whenInvincible;
        this.whenSpecialDotEat = whenSpecialDotEat;
        this.lastTimeDirection = lastTimeDirection;
        if (!isAlive) super.setIsAlive(false);
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
    public long getSpecialDotEatenTime() {
        return whenSpecialDotEat;
    }

    @Override
    public MatrixCoordinates getMatrixCoordinates() {
        return this.movementManager.currentMatrixCoordinates();
    }

    @Override
    public Direction getDirection() {
        return this.movementManager.getCurrentDirection();
    }

    // TODO: Handling the part of the AI pacman.
    @Override
    public void update(GameContext currentContext) {
        Set<Collision> collision = currentContext.getCollisions(this);
        Stream<Ghost> ghosts = collision.stream().filter(x -> x.getGameEntity() instanceof Ghost).map(x -> (Ghost) x.getGameEntity());
        ghosts.findFirst().ifPresent(x -> checkCollisionWithGhost(x, currentContext));
        if (this.isAlive()) {
            collision.stream()
                .filter(x -> x.getGameEntity() instanceof Dot)
                .map(x -> (Dot) x.getGameEntity())
                .findFirst()
                .ifPresent(x -> checkSpecialDot(x, currentContext));
            checkPacmanUpdates(currentContext);
            super.setPosition(this.movementManager.move());
        }
    }

    /* Checks if it is necessary to change the pacman state. */
    private void checkPacmanUpdates(GameContext context) {
        long currentTime = context.getGameState().getTimeLeftInMillis();
        if (this.invincible && currentTime <= this.whenInvincible - TIME_INVINCIBILITY) {
            this.invincible = false;
        }
        if (this.canEatGhosts && currentTime <= this.whenSpecialDotEat - TIME_CAN_EAT_GHOSTS) {
            this.canEatGhosts = false;
        }
        if (!this.controlledByPlayer && currentTime <= this.lastTimeDirection - TIME_CHANGE_DIRECTION) {
            movementBehaviour(context);
        }
    }

    /* Chooses how to move the pacman if no user commands it,
    by checking its neighbors or otherwise gets random direction. */
    private void movementBehaviour(GameContext context) {
        GameMap map = context.getMap();
        //boolean found = false;
        Map<MatrixCoordinates, Direction> md = MovableEntity
            .getWalkableDirection(this.movementManager.currentMatrixCoordinates(), map);
        List<Tile> tiles = md.entrySet()
            .stream()
            .map(x -> map.getTile(x.getKey()))
            .filter(
                y -> y.getTileType() == TileType.DOT ||
                y.getTileType() == TileType.SPECIAL_DOT
            ).collect(
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    collected -> {
                        Collections.shuffle(collected);
                        return collected;
                    }
                )
            );
        if (tiles.isEmpty()) {
            this.movementManager.changeDirection(MovableEntity.getRandomDirection());
        } else {
            this.movementManager.changeDirection(md.get(tiles.getFirst().getMatrixPosition()));
        }
        // for (int i = 0; i < Direction.values().length - 1 && !found; i++) {
        //     Direction direction = Direction.values()[i];
        //     MatrixCoordinates tile = this.movementManager.currentMatrixCoordinates()
        //         .getNeighbour(direction, map.getGridSize());
        //     Tile mapTile = map.getTile(tile);
        //     if (mapTile.getTileType() == TileType.DOT || mapTile.getTileType() == TileType.SPECIAL_DOT) {
        //         found = true;
        //         this.movementManager.changeDirection(direction);
        //     }
        // }
        // if (!found) {
        //     this.movementManager.changeDirection(MovableEntity.getRandomDirection());
        // }
        this.lastTimeDirection = context.getGameState().getTimeLeftInMillis();
    }

    private void checkCollisionWithGhost(Ghost ghost, GameContext context) {
        if (!this.canEatGhosts && !this.invincible) {
            if (this.lives > 0) {
                this.lives = this.lives - 1;
                this.invincible = true;
                this.whenInvincible = context.getGameState().getTimeLeftInMillis();
                List<Tile> tiles = context.getMap().getPacmanSpawnPoints().stream().toList();
                super.setPosition(tiles.get(new Random().nextInt(0, tiles.size())).getCenterPosition());
            } else {
                super.setIsAlive(false);
            }
        } else {
            if (isAlive() && this.canEatGhosts) {
                this.score = this.score + ghost.getGhostValue();
            }
        }
    }

    /* Checks if the collision made with a dot and if the dot is special. */
    private void checkSpecialDot(Dot dot, GameContext context) {
        if (dot.isSpecial()) {
            this.canEatGhosts = true;
            this.whenSpecialDotEat = context.getGameState().getTimeLeftInMillis();
        }
        this.score = this.score + dot.dotValue();
    }

    @Override
    public boolean isAlive() {
        return this.lives > 0;
    }
}
