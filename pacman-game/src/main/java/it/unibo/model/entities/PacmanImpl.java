package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Direction;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.Tile;

import java.util.UUID;

public class PacmanImpl extends GameEntityImpl implements Pacman{

    private final static int NUMBER_LIVES = 3;
    private final UUID id;
    private int score;
    private int lives;
    private boolean controlledByPlayer;
    private boolean ghostCanBeEaten;
    private Direction direction;

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

    }

    @Override
    public boolean isPlayer() {
        return this.controlledByPlayer;
    }

    @Override
    public boolean canEatGhost() {
        return this.ghostCanBeEaten;
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

    }

    @Override
    public Vector2D getPosition() {
        return super.getPosition();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return super.getBoundingBox();
    }

    @Override
    public boolean isAlive() {
        return super.isAlive();
    }
}
