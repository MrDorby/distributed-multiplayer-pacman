package it.unibo.model;

import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;
import it.unibo.model.entities.DotImpl;
import it.unibo.model.entities.GameEntityFactory;
import it.unibo.model.entities.Pacman;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.FourPlayersGameMap;
import it.unibo.model.map.Tile;

import java.util.*;

import it.unibo.model.map.TileImpl;
import it.unibo.model.map.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Write it better.
public class GameEntitiesTest {

    private static final int NUMBER_LIVES = 3;
    private GameEntityFactory gameFactory;
    private GameMap map;
    private Pacman pacman;

    @BeforeEach
    public void start() {
        int x = 0, y = 0;
        MatrixCoordinates start = new MatrixCoordinates(x, y);
        this.createMap(start);
        this.pacman = gameFactory.createPacman(this.map.getTile(start));
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
        MatrixCoordinates dotMatrix = new MatrixCoordinates(x, y);
        Dot dot = new DotImpl(new TileImpl(dotMatrix, dotCentre, Optional.empty(), TileType.SIMPLE));
        int initialScore = 0;
        assertEquals(initialScore, pacman.getScore());
        pacman.move(Direction.RIGHT);
        //GameContext gameContext = new GameContextImpl();
        //pacman.update();
        assertEquals(++initialScore, pacman.getScore());
    }

    @Test
    public void hungryPacman() {
        int x = 1, y = 0;
        Vector2D ghostCentre = new Vector2D(x, y);
        //Ghost ghost = new GhostImpl(new Tile(ghostCentre));
        //assertFalse(pacman.canEatGhost());
        //pacman.move(Direction.RIGHT);
        //pacman.update();
    }

    private void createMap(MatrixCoordinates start) {
        int x = start.row(), y = start.column(), numberTiles = 2;
        Map<MatrixCoordinates, Tile> tiles = new HashMap<>();
        for (int i = 0; i < numberTiles; i++, x++) {
            MatrixCoordinates matrixPosition = new MatrixCoordinates(x, y);
            Vector2D centrePosition = new Vector2D(x, y);
            tiles.put(matrixPosition, new TileImpl(matrixPosition, centrePosition, Optional.empty(), TileType.SIMPLE));
        }
        map = new FourPlayersGameMap(tiles, new MatrixCoordinates(2, 1));
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
