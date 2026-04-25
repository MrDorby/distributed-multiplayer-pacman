package it.unibo.model.entities;

import it.unibo.model.collisions.BoundingBox;
import it.unibo.model.common.Direction;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.Tile;

public class GhostImpl extends GameEntityImpl implements Ghost {

    private Direction direction;

    public GhostImpl(Tile tile) {
        super(tile);
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void update(GameContext currentContext) {

    }

}
