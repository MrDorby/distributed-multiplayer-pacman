package it.unibo.model.entities;

import it.unibo.model.common.Direction;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextImpl;
import it.unibo.model.map.Tile;

import java.util.Optional;
import java.util.Set;

import it.unibo.model.map.TileImpl;
import it.unibo.model.map.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//TODO: Write it better.
public class GameEntitiesTest {

    private static final int NUMBER_LIVES = 3;
    private GameEntityFactory gameFactory;
    private Pacman pacman;

    @BeforeEach
    public void start() {
        int x = 0, y = 0;
        Vector2D start = new Vector2D(x, y);
        this.pacman = gameFactory.createPacman(createTile(start));
    }

    @Test
    public void initialPacmanInstance() {
        assertTrue(this.pacman.isAlive());
        assertEquals(NUMBER_LIVES, this.pacman.getLives());
    }

    @Test
    public void movePacman() {
        int x = 0, y = 0;
        Vector2D initialPosition = new Vector2D(x, y);
        assertEquals(initialPosition, this.pacman.getPosition());
        Direction direction = Direction.RIGHT;
        this.pacman.move(direction);
        //TODO: move
        assertEquals(nextPosition(direction, initialPosition), this.pacman.getPosition());
    }

    @Test
    public void scorePacman() {
        int x = 1, y = 0;
        Vector2D dotPosition = new Vector2D(x, y);
        //Dot dot = gameFactory.createDot(createTile(dotPosition));
        int initialScore = 0;
        assertEquals(initialScore, this.pacman.getScore());
        this.pacman.move(Direction.RIGHT);
        // 1) Move pacman,
        // 2) Create GameContext,
        // 3) Update,
        // 4) Call CollisionManager,
        // 5) Assign the collision,
        // 6) Update.
        // TODO: Instead the update I can call the CollisionManager (before I need to call the MovementManager)
        //GameContext gameContext = new GameContextImpl(null, Set.of(dot), Set.of(), Set.of(pacman), null);
        //this.pacman.update(gameContext);
        assertEquals(++initialScore, this.pacman.getScore());
    }

    @Test
    public void pacmanLosesLife() {
        int x = 1, y = 0;
        Vector2D ghostPosition = new Vector2D(x, y);
        //Ghost ghost = gameFactory.createGhost(createTile(ghostPosition));
        assertFalse(this.pacman.canEatGhost());
        //pacman.move(Direction.RIGHT);
        //pacman.update();
        // 1) Move pacman,
        // 2) Create GameContext,
        // 3) Update,
        // 4) Call CollisionManager,
        // 5) Assign the collision,
        // 6) Update.
        // Create a unique function for this block of operations.
    }

    private Tile createTile(Vector2D position) {
        return null;
        //return new TileImpl(position, position, Optional.empty(), TileType.SIMPLE);
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
}
