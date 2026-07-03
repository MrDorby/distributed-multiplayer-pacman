package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

/**
 * This {@code Ghost} implementation doesn't perform movement decisions nor collision handling,
 * moving only along the ghost's last set direction. It is able to respawn.
 */
public class SpeculativeGhost extends GhostImpl {
    public SpeculativeGhost(Tile tile, GameMap map) {
        super(tile, map);
    }

    @Override
    public void update(GameContext context) {
        if (this.isAlive()) {
            Vector2D nextPosition = this.movementManager.move();
            super.setPosition(nextPosition);
        } else {
            handleRespawn(context);
        }
    }
}
