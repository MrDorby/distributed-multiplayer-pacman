package it.unibo.model.entities;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Direction;
import it.unibo.model.common.GameConstants;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;
import it.unibo.model.movement.MovementManager;
import it.unibo.model.movement.MovementManagerImpl;

import java.util.Set;
import java.util.stream.Stream;

public class GhostImpl extends GameEntityImpl implements Ghost {

    private final static int TIME_TO_RESPAWN = 5000;  // 5000 millis
    private final MovementManager movementManager;
    private long lastTimeDead;
    private Direction direction;

    public GhostImpl(Tile tile, GameMap map) {
        super(tile);
        this.movementManager = new MovementManagerImpl(
                map,
                tile.getMatrixPosition(),
                GameConstants.GameEntityFeatures.GHOST.getVelocity()
        );
        this.direction = MovableEntity.getRandomDirection();
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void update(GameContext currentContext) {
        Set<Collision> collision = currentContext.getCollisions(this);
        Stream<Pacman> pacman = collision.stream().filter(x -> x.getGameEntity() instanceof Pacman).map(x -> (Pacman) x.getGameEntity());
        pacman.findFirst().ifPresent(x -> checkGhostCanBeEaten(x, currentContext));
        if (this.isAlive()) {
            if (currentContext.getGameState().getTimeLeft().toMillis() - this.lastTimeDead >= TIME_TO_RESPAWN) {
                this.setIsAlive(true);
            }
            Vector2D nextPosition = this.movementManager.move();
            if (getPosition() == nextPosition) {
                this.direction = MovableEntity.getRandomDirection();
            }
            setPosition(nextPosition);
        }
    }

    private void checkGhostCanBeEaten(Pacman pacman, GameContext context) {
        if (pacman.canEatGhost()) {
            this.setIsAlive(false);
            this.lastTimeDead = context.getGameState().getTimeLeft().toMillis();
        }
    }
}
