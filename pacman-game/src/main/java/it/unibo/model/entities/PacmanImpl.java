package it.unibo.model.entities;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Direction;
import it.unibo.model.common.GameConstants;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;
import it.unibo.model.movement.MovementManager;
import it.unibo.model.movement.MovementManagerImpl;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PacmanImpl extends GameEntityImpl implements Pacman {

    private final static int NUMBER_LIVES = 3;
    private final static int TIME_CAN_EAT_GHOSTS = 3000;  // 5000 millis
    private final MovementManager movementManager;
    private String id;
    private int score;
    private int lives;
    private boolean controlledByPlayer;
    private boolean canEatGhosts;
    private long whenSpecialDotEat;
    private Direction direction;

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
    public Direction getDirection() {
        return this.direction;
    }

    // TODO: Handle time for pacman not controlled
    @Override
    public void update(GameContext currentContext) {
        Set<Collision> collision = currentContext.getCollisions(this);
        Stream<Ghost> ghosts = collision.stream().filter(x -> x.getGameEntity() instanceof Ghost).map(x -> (Ghost) x.getGameEntity());
        ghosts.findFirst().ifPresent(x -> checkPlayerIsAlive(x, currentContext));
        if (this.isAlive()) {
            collision.stream()
                .filter(x -> x.getGameEntity() instanceof Dot)
                .map(x -> (Dot) x.getGameEntity())
                .findFirst()
                .ifPresent(x -> checkSpecialDot(x, currentContext));
            long currentTime = currentContext.getGameState().getTimeLeft().toMillis();
            if (this.canEatGhosts && currentTime <= this.whenSpecialDotEat - TIME_CAN_EAT_GHOSTS) {
                this.canEatGhosts = false;
            }
            if (!this.controlledByPlayer) {
                movementBehaviour(currentContext);
            }
            super.setPosition(this.movementManager.move());
        }
    }

    /* Chooses how to move the pacman if no user command it, 
    by checking its neighbours or otherwise gets random direction. */
    private void movementBehaviour(GameContext context) {
        GameMap map = context.getMap();
        Direction direction;
        boolean found = false;
        for (int i = 0; i < Direction.values().length - 1 && !found; i++) {
            MatrixCoordinates tile = this.movementManager.currentMatrixCoordinates()
                .getNeighbour(Direction.values()[i], map.getGridSize());
            direction = Direction.values()[i];
            if (!map.getTile(tile).getDot().isEmpty()) {
                found = true;
                this.movementManager.changeDirection(direction);
            }
        }
        if (!found) {
            this.movementManager.changeDirection(MovableEntity.getRandomDirection());
        }
    }

    // TODO: modify the way of spawning the pacmans. Set invincibility for pacman when get eaten.
    private void checkPlayerIsAlive(Ghost ghost, GameContext context) {
        if (!this.canEatGhosts) {
            if (this.lives > 0) {
                this.lives = this.lives - 1;
                List<Tile> tiles = context.getMap().getPacmanSpawnPoints().stream().collect(Collectors.toList());
                super.setPosition(tiles.get(new Random().nextInt(0, tiles.size())).getCenterPosition());
            } else {
                super.setIsAlive(false);
            }
        } else {
            this.score = this.score + ghost.getGhostValue();
        }
    }

    /* Checks if the collision made with a dot and if the dot is special. */
    private void checkSpecialDot(Dot dot, GameContext context) {
        if (dot.isSpecial()) {
            this.canEatGhosts = true;
            this.whenSpecialDotEat = context.getGameState().getTimeLeft().toMillis();
        }
        this.score = this.score + dot.dotValue();
    }

    @Override
    public boolean isAlive() {
        return this.lives > 0;
    }
}
