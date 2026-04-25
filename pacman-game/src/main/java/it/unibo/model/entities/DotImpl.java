package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.Tile;

public class DotImpl extends GameEntityImpl implements Dot {

    public DotImpl(Tile tile) {
        super(tile);
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public void update(GameContext currentContext) {

    }
}
