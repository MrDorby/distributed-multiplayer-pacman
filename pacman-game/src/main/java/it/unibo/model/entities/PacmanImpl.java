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

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

public class PacmanImpl extends GameEntityImpl implements Pacman{

    private final static int NUMBER_LIVES = 3;
    private final static int TIME_CAN_EAT_GHOSTS = 5000;  // 5000 millis
    private final MovementManager movementManager;
    private String id;
    private int score;
    private int lives;
    private boolean controlledByPlayer;
    private boolean canEatGhosts;
    private long whenSpecialDotEat;
    private Direction direction;
    private Direction actualDirection;

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

    // TODO: Handling the part of the AI pacman.
    @Override
    public void update(GameContext currentContext) {
        Set<Collision> collision = currentContext.getCollisions(this);
        Stream<Ghost> ghosts = collision.stream().filter(x -> x.getGameEntity() instanceof Ghost).map(x -> (Ghost) x.getGameEntity());
        ghosts.findFirst().ifPresent(_ -> checkPlayerIsAlive());
        if (this.isAlive()) {
            Stream<Dot> dots = collision.stream().filter(x -> x.getGameEntity() instanceof Dot).map(x -> (Dot) x.getGameEntity());
            dots.findFirst().ifPresent(x -> checkSpecialDot(x, currentContext));
            if (currentContext.getGameState().getTimeLeft().toMillis() - this.whenSpecialDotEat >= TIME_CAN_EAT_GHOSTS) {
                this.canEatGhosts = false;
            }
            if (!this.controlledByPlayer) {
                Vector2D position = getPosition();
                // TODO: get the tile that contains the pacman to choose the next direction based on dot and neighbor.
                this.movementManager.changeDirection(MovableEntity.getRandomDirection());
            }
            super.setPosition(this.movementManager.move());
        }
    }

    private void checkPlayerIsAlive() {
        if (!this.canEatGhosts) {
            if (this.lives > 0) {
                this.lives = this.lives - 1;
            } else {
                super.setIsAlive(false);
            }
        }
    }

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
