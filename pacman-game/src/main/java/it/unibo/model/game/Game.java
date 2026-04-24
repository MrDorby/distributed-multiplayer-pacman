package it.unibo.model.game;

import it.unibo.model.common.Direction;
import it.unibo.model.entities.Pacman;

import java.time.Duration;

public interface Game {
    GameContext getContext();
    void update(Duration timeLeft);
    void movePacman(Pacman pacman, Direction direction);
    void changePacmanBehaviour(Pacman pacman, boolean isPlayer);
}
