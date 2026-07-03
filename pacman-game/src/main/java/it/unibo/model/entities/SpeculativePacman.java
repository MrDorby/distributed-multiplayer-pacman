package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

/**
 * This {@code Pacman} implementation doesn't perform collision handling, moving only
 * along the pacman's last set direction if it is a bot, but it checks timing expirations.
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
