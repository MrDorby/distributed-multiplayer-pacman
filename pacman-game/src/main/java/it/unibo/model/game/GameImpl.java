package it.unibo.model.game;

import it.unibo.model.collisions.CollisionManager;
import it.unibo.model.common.Direction;
import it.unibo.model.entities.Pacman;

import java.time.Duration;
import java.util.UUID;

public class GameImpl implements Game {
    private final GameContext context;
    private final CollisionManager collisionManager;

    public GameImpl(GameContext initial, CollisionManager collisionManager) {
        this.context = initial;
        this.collisionManager = collisionManager;
    }

    @Override
    public GameContext getContext() {
        return context;
    }

    @Override
    public void update(Duration tickDelta) {
        if (context.getGameState().isGameOver()) {
            // System.out.println("Game is over, not actually updating");
            return;
        }
        context.decrementTime(tickDelta);
        context.setCollisions(collisionManager.computeCollisions(context));
        context.getGameEntities().forEach(entity -> entity.update(context));
        context.createGameState();
        // System.out.println(context.getGameState().getTimeLeft().toSeconds());
    }

    @Override
    public void movePacman(UUID pacmanId, Direction direction) {
        this.context.getPacmans().stream()
                .filter(pacman -> pacman.getId().equals(pacmanId))
                .findFirst()
                .ifPresent(pacman -> pacman.move(direction));
    }

    @Override
    public void changePacmanBehaviour(Pacman pacman, boolean isPlayer) {
        // TODO
    }
}
