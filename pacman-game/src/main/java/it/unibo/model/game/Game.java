package it.unibo.model.game;

import it.unibo.model.common.Direction;

import java.util.List;

public interface Game {

    /**
     * Returns the game context.
     */
    GameContext getContext();

    /**
     * Checks collisions and then updates all the game entities.
     */
    void update(long timeLeftInMillis);

    /**
     * Sets the next desired direction of a given pacman identified by the username.
     */
    void movePacman(String pacmanId, Direction direction);

    /**
     * Sets the name of each pacman.
     */
    void setPacmanNames(List<String> usernames);

    /**
     * Lets a bot take control of a given pacman in case the player controlling it goes afk.
     */
    void changePacmanBehaviour(String pacmanId, boolean isPlayer);
}
