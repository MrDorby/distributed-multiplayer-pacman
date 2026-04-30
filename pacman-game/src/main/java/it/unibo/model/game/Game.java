package it.unibo.model.game;

import it.unibo.model.common.Direction;
import it.unibo.model.entities.Pacman;

import java.time.Duration;
import java.util.UUID;

public interface Game {

    /**
     * Returns the game context.
     */
    GameContext getContext();

    /**
     * Checks collisions and then updates all the game entities.
     */
    void update(Duration timeLeft);

    /**
     * Sets the next desired direction of a given pacman identified by its UUID.
     */
    void movePacman(UUID pacmanId, Direction direction);

    /**
     * Lets a bot take control of a given pacman in case the player controlling it goes afk.
     */
    void changePacmanBehaviour(Pacman pacman, boolean isPlayer);
}
