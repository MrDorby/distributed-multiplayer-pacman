package it.unibo.test.entities;

import it.unibo.model.common.Direction;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;
import it.unibo.model.entities.Pacman;
import it.unibo.model.entities.PacmanImpl;
import it.unibo.model.map.Tile;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameEntitiesTest {

    private static final int NUMBER_LIVES = 3;

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
        Vector2D initialPosition = new Vector2D(x, y);
        assertEquals(initialPosition, pacman.getPosition());
        Direction direction = Direction.RIGHT;
        pacman.move(direction);
        assertEquals(nextPosition(direction, initialPosition), pacman.getPosition());
    }

    @Test
    public void scorePacman() {

    }

    @Test
    public void hungryPacman() {

    }

    private Pacman beginningPacman() {
        int x = 0, y = 0;
        Vector2D centre = new Vector2D(x, y);
        return new PacmanImpl(new TileForHelp(centre));
    }

    private Vector2D nextPosition(Direction direction, Vector2D initialPosition) {
        return switch (direction) {
            case UP -> new Vector2D(initialPosition.x(), initialPosition.y() - 1);
            case DOWN -> new Vector2D(initialPosition.x(), initialPosition.y() + 1);
            case LEFT -> new Vector2D(initialPosition.x() - 1, initialPosition.y());
            case RIGHT -> new Vector2D(initialPosition.x() + 1, initialPosition.y());
            default -> initialPosition;
        };
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
