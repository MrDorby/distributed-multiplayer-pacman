package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;

/**
 *  Models an entity in the game.
 */
public interface GameEntity {

    /**
     * Updates the state of the GameEntity according to the current GameContext.
     * @param currentContext
     */
    void update(GameContext currentContext);

    /**
     * @return the current poisiton as a Vector2D of the GameEntity.
     */
    Vector2D getPosition();

    /**
     * @return the current bounding box.
     */
    BoundingBox getBoundingBox();

    /**
     * @return a boolean that indicates if the GameEntity is alive or not.
     */
    boolean isAlive();
}
