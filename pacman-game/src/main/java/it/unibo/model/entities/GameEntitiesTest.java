package it.unibo.model.entities;

import it.unibo.model.common.Vector2D;
import it.unibo.model.map.Tile;

import java.util.Optional;

//import org.junit.jupiter.api.Test;

public class GameEntitiesTest {

    private GameEntity gameEntity;

    //@Test
    public void isPacmanInitiallyAlive() {
        int x = 0, y = 0;
        Vector2D centre = new Vector2D(x, y);
        gameEntity = new PacmanImpl(new TileForHelp(centre));
        //assertTrue(gameEntity.isAlive());
    }

    private class TileForHelp implements Tile {

        private final Vector2D centrePosition;

        TileForHelp(Vector2D centre) {
            this.centrePosition = centre;
        }

        @Override
        public boolean isWall() {
            return false;
        }

        @Override
        public Optional<Dot> getDot() {
            return Optional.empty();
        }

        @Override
        public Vector2D getCenterPosition() {
            return this.centrePosition;
        }

        @Override
        public Vector2D getMatrixPosition() {
            return null;
        }
    }

}
