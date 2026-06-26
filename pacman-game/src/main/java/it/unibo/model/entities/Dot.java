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
     * Returns the last time the dot was eaten in millis.
     */
    long getLastTimeEaten();

    /**
     * Sets if the dot has the special behaviour or not.
     * @param isSpecial a boolean representing the condition to set.
     */
    void setIsSpecial(boolean isSpecial);

    /**
     * @return the value of a single dot when the player catches it.
     */
    int dotValue();
}
