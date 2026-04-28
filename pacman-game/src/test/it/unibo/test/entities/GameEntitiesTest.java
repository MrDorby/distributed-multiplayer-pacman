package it.unibo.test.entities;

import it.unibo.model.collisions.Collision;
import it.unibo.model.common.Direction;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;
import it.unibo.model.entities.DotImpl;
import it.unibo.model.entities.Ghost;
import it.unibo.model.entities.GhostImpl;
import it.unibo.model.entities.Pacman;
import it.unibo.model.entities.PacmanImpl;
import it.unibo.model.entities.GameEntity;
import it.unibo.model.map.Tile;
import it.unibo.model.game.GameContext;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameEntitiesTest {

    private static final int NUMBER_LIVES = 3;
    private Pacman pacman;

    @BeforeEach
    public void beginningPacman() {
        int x = 0, y = 0;
        Vector2D centre = new Vector2D(x, y);
        this.pacman = new PacmanImpl(new TileForHelp(centre));
    }

    @Test
    public void initialPacmanInstance() {
        assertTrue(pacman.isAlive());
        assertEquals(NUMBER_LIVES, pacman.getLives());
    }

    @Test
    public void movePacman() {
        int x = 0, y = 0;
        Vector2D initialPosition = new Vector2D(x, y);
        assertEquals(initialPosition, pacman.getPosition());
        Direction direction = Direction.RIGHT;
        pacman.move(direction);
        //pacman.update();
        assertEquals(nextPosition(direction, initialPosition), pacman.getPosition());
    }

    @Test
    public void scorePacman() {
        int x = 1, y = 0;
        Vector2D dotCentre = new Vector2D(x, y);
        Dot dot = new DotImpl(new TileForHelp(dotCentre));
        int initialScore = 0;
        assertEquals(initialScore, pacman.getScore());
        pacman.move(Direction.RIGHT);
        //pacman.update();
        assertEquals(++initialScore, pacman.getScore());
    }

    @Test
    public void hungryPacman() {
        int x = 1, y = 0;
        Vector2D ghostCentre = new Vector2D(x, y);
        Ghost ghost = new GhostImpl(new TileForHelp(ghostCentre));
        assertFalse(pacman.canEatGhost());
        pacman.move(Direction.RIGHT);
        //pacman.update();
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

    private static class GameContextForHelp implements GameContext {

        Set<Collision> collisions;

        GameContextForHelp(Set<GameEntity> gameEntity) {
            // gameEntity.stream().forEach(x -> collisions.add());
        }
    }
}
