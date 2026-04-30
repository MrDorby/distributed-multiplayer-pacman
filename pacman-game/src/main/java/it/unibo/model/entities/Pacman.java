package it.unibo.model.entities;

import it.unibo.model.common.Direction;

import java.util.UUID;

/**
 * Models the concept of a pacman player.
 */
public interface Pacman extends MovableEntity {

    /**
     * @return UUID, the identifier of the pacman.
     */
    UUID getId();

    /**
     * @return the score of the pacman.
     */
    int getScore();

    /**
     * @return the number of pacman's lives.
     */
    int getLives();

    /**
     * Lets the pacman moves in the direction chosen by the "player".
     * @param direction, where to move the pacman.
     */
    void move(Direction direction);

    /**
     * @return true if the pacman is controlled by a player and false otherwise.
     */
    boolean isPlayer();

    /**
     * @return true if the pacman can eat the ghosts and false otherwise.
     */
    boolean canEatGhost();

    /**
     * This method changes the behaviour of the pacman.
     * @param isPlayer true if controlled by a real-player false otherwise.
     */
    void changeBehaviour(boolean isPlayer);
}
