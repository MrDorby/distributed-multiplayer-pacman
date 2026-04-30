package it.unibo.model.game;

import it.unibo.model.collisions.CollisionManager;
import it.unibo.model.common.Direction;
import it.unibo.model.entities.Pacman;

import java.time.Duration;
import java.util.UUID;

public class GameImpl implements Game {
    private final GameContext context;

    public GameImpl(GameContext initial) {
        this.context = initial;
    }

    @Override
    public GameContext getContext() {
        return context;
    }

    @Override
    public void update(Duration timeLeft) {
        // TODO check collisions
        context.getGameEntities().forEach(entity -> entity.update(context));
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
