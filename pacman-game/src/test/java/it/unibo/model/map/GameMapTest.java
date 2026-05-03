package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

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
        // TODO: write test
    }

    @Test
    void testGhostSpawnPoint() {
        // TODO: write test
    }

    // TODO: fill these parameters
    //  Understand how to pass an enum type as a CSV source parameter.
    @ParameterizedTest
    @CsvSource({
        //"0, 0, " + TileType.WALL  -> DOES NOT WORK
    })
    void testTiles(int tileRow, int tileCol, TileType expectedType) {
        // TODO: write test
    }
}
