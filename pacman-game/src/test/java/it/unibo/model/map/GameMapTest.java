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
            "more_pacman_map.json",
            "less_pacman_map.json"
    })
    void testWrongMapFileFormat(String filePath) {
        assertThrows(IllegalArgumentException.class, () -> mapFactory.fromJSON(RESOURCES_PATH + filePath));
    }

    @Test
    void testCorrectMap() {
        assertDoesNotThrow(() -> mapFactory.fromJSON(RESOURCES_PATH + "correct_map.json"));
    }

    @Test
    void testPacmanSpawnPoints() {
        Set<Tile> pacmanSpawnPoints = Set.of(
                instantiateTile(new MatrixCoordinates(1, 3), TileType.PACMAN_SPAWN),
                instantiateTile(new MatrixCoordinates(1, 5), TileType.PACMAN_SPAWN),
                instantiateTile(new MatrixCoordinates(9, 3), TileType.PACMAN_SPAWN),
                instantiateTile(new MatrixCoordinates(9, 7), TileType.PACMAN_SPAWN)
        );
        GameMap map = mapFactory.fromJSON(RESOURCES_PATH + "correct_map.json");
        assertEquals(pacmanSpawnPoints, map.getPacmanSpawnPoints());
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
                coordinates.row() * TILE_SIZE + TILE_SIZE / 2,
                coordinates.column() * TILE_SIZE + TILE_SIZE / 2
        );
    }

    @Test
    void testGhostSpawnPoint() {
        Tile ghostSpawnPoint = instantiateTile(new MatrixCoordinates(5, 5), TileType.GHOST_SPAWN);
        GameMap map = mapFactory.fromJSON(RESOURCES_PATH + "correct_map.json");
        assertEquals(ghostSpawnPoint, map.getGhostSpawnPoint());
    }

    // TODO: fill these parameters
    @ParameterizedTest
    @CsvSource({
        "0, 0, WALL"
    })
    void testTiles(int tileRow, int tileCol, TileType expectedType) {
        // TODO: write test
    }
}
