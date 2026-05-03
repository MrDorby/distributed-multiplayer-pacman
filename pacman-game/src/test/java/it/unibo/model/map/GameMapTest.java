package it.unibo.model.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameMapTest {
    private static final String RESOURCES_PATH = "it/unibo/maps/";

    private GameMapFactory mapFactory;

    @BeforeEach
    void init() {
        this.mapFactory = new FourPlayersGameMapFactory();
    }

    @Test
    void testWrongMapSize() {
        var map = mapFactory.fromJSON(RESOURCES_PATH + "wrong_size_map.json");
        assertTrue(map.isEmpty());
    }
}
