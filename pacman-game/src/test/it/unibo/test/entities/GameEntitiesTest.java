package it.unibo.test.entities;

import it.unibo.model.common.Direction;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;
import it.unibo.model.entities.GameEntity;
import it.unibo.model.entities.Pacman;
import it.unibo.model.entities.PacmanImpl;
import it.unibo.model.map.Tile;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameEntitiesTest {

    private static int NUMBER_LIVES = 3;

    @Test
    public void initialPacmanInstance() {
        Pacman pacman = beginningPacman();
        assertTrue(pacman.isAlive());
        assertEquals(NUMBER_LIVES, pacman.getLives());
    }

    @Test
    public void movePacmanRight() {
        int x = 0, y = 0;
        Pacman pacman = beginningPacman();
        assertEquals(new Vector2D(x, y), pacman.getPosition());
        pacman.move(Direction.RIGHT);
        assertEquals(new Vector2D(x + 1, y), pacman.getPosition());
    }

    private Pacman beginningPacman() {
        int x = 0, y = 0;
        Vector2D centre = new Vector2D(x, y);
        return new PacmanImpl(new TileForHelp(centre));
    }

    private static class TileForHelp implements Tile {

        private final Vector2D centrePosition;

        TileForHelp(Vector2D centre) {
            this.centrePosition = centre;
        }

        public boolean isWall() {
            return false;
        }

        public Optional<Dot> getDot() {
            return Optional.empty();
        }

        public Vector2D getCenterPosition() {
            return this.centrePosition;
        }

        public Vector2D getMatrixPosition() {
            return null;
        }
    }

}
