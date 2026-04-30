package it.unibo.model.entities;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Direction;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.Tile;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class PacmanImpl extends GameEntityImpl implements Pacman{

    private final static int NUMBER_LIVES = 3;
    private final static int TIME_CAN_EAT_GHOSTS = 5;
    private final UUID id;
    private int score;
    private int lives;
    private boolean controlledByPlayer;
    private boolean canEatGhosts;
    private long whenSpecialDotEat;
    private Direction direction;
    private Direction previousDirection;

    public PacmanImpl(Tile tile) {
        super(tile);
        this.id = UUID.randomUUID();
        this.lives = NUMBER_LIVES;
        this.controlledByPlayer = true;
    }

    @Override
    public UUID getId() {
        return this.id;
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
        this.previousDirection = this.direction;
        this.direction = direction;     // TODO: Is it right to do so?
        //changeDirection(direction);     // The MovementManager needs to calculate the position?
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

    @Override
    public void update(GameContext currentContext) {
        // TODO: Understand if here the pacman needs to check possible collision and
        // accordingly move to the direction
        Set<Collision> collision = currentContext.getCollisions(this);
        Stream<Ghost> ghosts = collision.stream().filter(x -> x.getGameEntity() instanceof Ghost).map(x -> (Ghost) x.getGameEntity());
        ghosts.findFirst().ifPresent(_ -> checkPlayerIsAlive());
        if (this.isAlive()) {
            Stream<Dot> dots = collision.stream().filter(x -> x.getGameEntity() instanceof Dot).map(x -> (Dot) x.getGameEntity());
            dots.findFirst().ifPresent(this::checkSpecialDot);
        }
        if (System.currentTimeMillis() - this.whenSpecialDotEat >= TIME_CAN_EAT_GHOSTS) {
            this.canEatGhosts = false;
        }

        // TODO: At the end, invoking the method move from MovementManager.
        // move();
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

    private void checkSpecialDot(Dot dot) {
        if (dot.isSpecial()) {
            this.canEatGhosts = true;
            this.whenSpecialDotEat = System.currentTimeMillis();
        }
        this.score = this.score + dot.dotValue();
    }

    @Override
    public boolean isAlive() {
        return this.lives > 0;
    }
}
