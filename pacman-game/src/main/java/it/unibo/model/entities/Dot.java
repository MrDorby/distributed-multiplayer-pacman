package it.unibo.model.entities;

/**
 * Models the concept of a dot, a GameEntity used to recreate the points in the game.
 */
public interface Dot extends GameEntity {

    /**
     * This method return true if the dot is special or else otherwise.
     * @return a boolean.
     */
    boolean isSpecial();

    /**
     * @return the value of a single dot when the player catches it.
     */
    int dotValue();
}
