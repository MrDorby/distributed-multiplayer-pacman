package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;
import java.util.Set;

import static it.unibo.model.common.GameConstants.TILE_SIZE;
import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {
    private static final String RESOURCES_PATH = "it/unibo/maps/";
    public static final String CORRECT_MAP_PATH = RESOURCES_PATH + "correct_map.json";
    private static final MatrixCoordinates CORRECT_MAP_SIZE = new MatrixCoordinates(11, 11);

    private GameMapFactory mapFactory;

    @BeforeEach
    void init() {
        this.mapFactory = new FourPlayersGameMapFactory();
    }

    @ParameterizedTest
    @CsvSource({
            "wrong_size_map.json",
            "missing_fields_map.json",
            "invalid_character_map.json",
            "no_ghosts_map.json",
            "more_ghosts_map.json",
            "more_pacman_map.json",
            "less_pacman_map.json"
    })
    void testWrongMapFileFormat(String filePath) {
        assertThrows(IllegalArgumentException.class, () -> mapFactory.fromJSON(RESOURCES_PATH + filePath));
    }

    @Test
    void testCorrectMap() {
        assertDoesNotThrow(() -> mapFactory.fromJSON(CORRECT_MAP_PATH));
    }

    @Test
    void testMapGridSize() {
        assertEquals(CORRECT_MAP_SIZE, mapFactory.fromJSON(CORRECT_MAP_PATH).getGridSize());
    }

    @Test
    void testMapSize() {
        assertEquals(
                new Vector2D(CORRECT_MAP_SIZE.column() * TILE_SIZE, CORRECT_MAP_SIZE.row() * TILE_SIZE),
                mapFactory.fromJSON(CORRECT_MAP_PATH).getSize());
    }

    @Test
    void testPacmanSpawnPoints() {
        Set<Tile> pacmanSpawnPoints = Set.of(
                instantiateTile(new MatrixCoordinates(1, 3), TileType.PACMAN_SPAWN),
                instantiateTile(new MatrixCoordinates(1, 5), TileType.PACMAN_SPAWN),
                instantiateTile(new MatrixCoordinates(9, 3), TileType.PACMAN_SPAWN),
                instantiateTile(new MatrixCoordinates(9, 7), TileType.PACMAN_SPAWN)
        );
        GameMap map = mapFactory.fromJSON(CORRECT_MAP_PATH);
        assertEquals(pacmanSpawnPoints, map.getPacmanSpawnPoints());
    }

    @Test
    void testGhostSpawnPoint() {
        Tile ghostSpawnPoint = instantiateTile(new MatrixCoordinates(5, 5), TileType.GHOST_SPAWN);
        GameMap map = mapFactory.fromJSON(CORRECT_MAP_PATH);
        assertEquals(ghostSpawnPoint, map.getGhostSpawnPoint());
    }

    private Tile instantiateTile(MatrixCoordinates coordinates, TileType type) {
        return new TileImpl(
                coordinates,
                getCenterPosition(coordinates),
                Optional.empty(),
                type
        );
    }

    private Vector2D getCenterPosition(MatrixCoordinates coordinates) {
        return new Vector2D(
                coordinates.column() * TILE_SIZE + TILE_SIZE / 2,
                coordinates.row() * TILE_SIZE + TILE_SIZE / 2
        );
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, WALL",
            "1, 1, SIMPLE",
            "1, 3, PACMAN_SPAWN",
            "5, 5, GHOST_SPAWN",
            "7, 1, SIMPLE",
            "4, 5, SIMPLE",
            "10, 10, WALL",
            "0, 10, WALL",
            "10, 0, WALL",
            "5, 0, SIMPLE"
    })
    void testGetTile(int tileRow, int tileCol, TileType expectedType) {
        GameMap map = mapFactory.fromJSON(CORRECT_MAP_PATH);
        assertEquals(expectedType, map.getTile(new MatrixCoordinates(tileRow, tileCol)).getTileType());
    }

    @ParameterizedTest
    @CsvSource({
            "-1, -1",
            "13, 0",
            "0, 100",
            "30, 30"
    })
    void testOutOfBoundsTile(int tileRow, int tileCol) {
        GameMap map = mapFactory.fromJSON(CORRECT_MAP_PATH);
        assertThrows(IndexOutOfBoundsException.class, () -> map.getTile(new MatrixCoordinates(tileRow, tileCol)));
    }

    // TODO: also implement tests on the Tiles' Dots (present/not present) and their types (is Special or not)
}
