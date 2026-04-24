package it.unibo.model.game;

import it.unibo.model.common.Direction;
import it.unibo.model.entities.Pacman;

import java.time.Duration;

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

    }

    @Override
    public void movePacman(Pacman pacman, Direction direction) {

    }

    @Override
    public void changePacmanBehaviour(Pacman pacman, boolean isPlayer) {

    }
}
