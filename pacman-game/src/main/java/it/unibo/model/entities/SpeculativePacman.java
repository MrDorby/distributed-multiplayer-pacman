package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

/**
 * This {@code Pacman} implementation doesn't perform movement decisions nor
 * collision handling, moving both human players and bots along their last set direction.
 * However, it does check timing expirations.
 */
public class SpeculativePacman extends PacmanImpl {
    public SpeculativePacman(Tile tile, GameMap map) {
        super(tile, map);
    }

    @Override
    public void update(GameContext context) {
        if (this.isAlive()) {
            checkStateExpirations(context);
            Vector2D nextPosition = this.movementManager.move();
            super.setPosition(nextPosition);
        }
    }
}
