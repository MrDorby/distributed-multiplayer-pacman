package it.unibo.model.entities;

import it.unibo.model.collisions.CollisionManager;
import it.unibo.model.collisions.CollisionManagerImpl;
import it.unibo.model.common.Direction;
import it.unibo.model.common.GameConstants;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.game.GameContextImpl;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.FourPlayersGameMap;
import it.unibo.model.map.Tile;

import java.util.*;

import it.unibo.model.map.TileImpl;
import it.unibo.model.map.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameEntitiesTest {

    private static final int NUMBER_LIVES = 3;
    private static final MatrixCoordinates PACMAN_INIT_COORDINATES = new MatrixCoordinates(0, 0);
    private static final int GAME_DURATION_IN_MILLIS = 30000;
    private GameEntityFactory gameFactory;
    private GameMap map;
    private Pacman pacman;
    private GameContextImpl context;
    private CollisionManager collisionManager;

    @BeforeEach
    public void start() {
        this.gameFactory = new GameEntityFactoryImpl();
        this.createMap();
        this.pacman = this.gameFactory.createPacman(
            this.map.getTile(PACMAN_INIT_COORDINATES), this.map);
        this.collisionManager = new CollisionManagerImpl();
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
        createGameContext(Map.of(), Set.of(), Set.of(pacman));
        pacman.move(direction);
        pacman.update(context);
        assertEquals(nextPosition(direction, initialPosition), pacman.getPosition());
    }

    @Test
    public void scorePacman() {
        int initialScore = 0;
        assertEquals(initialScore, pacman.getScore());
        int x = 1, y = 0;
        Vector2D dotCentre = new Vector2D(x, y);
        Dot dot = new DotImpl(dotCentre);
        int dotValue = 1;
        assertEquals(dotValue, dot.dotValue());
        Direction direction = Direction.RIGHT;
        pacman.move(direction);
        createGameContext(Map.of(new MatrixCoordinates(0,1), dot), Set.of(), Set.of(pacman));
        pacman.update(context);
        context.setCollisions(collisionManager.computeCollisions(context));
        pacman.update(context);     // Check the collisions.
        assertEquals(++initialScore, pacman.getScore());
    }

    @Test
    public void GhostDamagesPacman() {
        assertFalse(pacman.canEatGhost());
        int initialLives = 3;
        assertEquals(initialLives, pacman.getLives());
        int x = 0, y = 2;
        Tile tile = new TileImpl(new MatrixCoordinates(0,0), new Vector2D(x, y), TileType.EMPTY);
        Ghost ghost = new GhostImpl(tile, map);
        pacman.move(Direction.RIGHT);
        createGameContext(Map.of(), Set.of(ghost), Set.of(pacman));
        pacman.update(context);
        pacman.update(context);     // We move again the pacman because after that will collide to the ghost.
        context.setCollisions(collisionManager.computeCollisions(context));
        pacman.update(context);     // Check the collisions.
        assertEquals(--initialLives, pacman.getLives());
    }

    /* Creation of a simple game map. */
    private void createMap() {
        int row = 2, column = 3;
        Map<MatrixCoordinates, Tile> tiles = new HashMap<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                MatrixCoordinates matrixPosition = new MatrixCoordinates(i, j);
                Vector2D centrePosition = new Vector2D(i, j);
                TileType type = j % 2 == 0 ? TileType.PACMAN_SPAWN : TileType.EMPTY;
                if (i == 0 && j == 1) {
                    type = TileType.GHOST_SPAWN;
                }
                tiles.put(matrixPosition, new TileImpl(matrixPosition, centrePosition, type));
            }
        }
        map = new FourPlayersGameMap(tiles, new MatrixCoordinates(row, column));
    }

    /* Simple computation of the next position. */
    private Vector2D nextPosition(Direction direction, Vector2D initialPosition) {
        int move = GameConstants.GameEntityFeatures.PACMAN.getVelocity();
        return switch (direction) {
            case UP -> new Vector2D(initialPosition.x(), initialPosition.y() - move);
            case DOWN -> new Vector2D(initialPosition.x(), initialPosition.y() + move);
            case LEFT -> new Vector2D(initialPosition.x() - move, initialPosition.y());
            case RIGHT -> new Vector2D(initialPosition.x() + move, initialPosition.y());
            default -> initialPosition;
        };
    }

    /* Provides fast implementation of the game context. */
    private void createGameContext(Map<MatrixCoordinates,Dot> dots, Set<Ghost> ghosts, Set<Pacman> pacmans) {
        this.context = new GameContextImpl(map, dots, 
                        ghosts, 
                        pacmans, 
                        GAME_DURATION_IN_MILLIS);
    }
}
