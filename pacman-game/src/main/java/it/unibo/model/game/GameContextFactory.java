package it.unibo.model.game;

import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.*;
import it.unibo.model.map.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static it.unibo.model.common.GameConstants.GAME_DURATION_SECONDS;
import static it.unibo.model.common.GameConstants.TILE_SIZE;

public class GameContextFactory {
    public static GameContext getTestContext() {

        Set<Tile> tiles = new HashSet<>();
        Set<Dot> dots = new HashSet<>();

        int counter = 0;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                TileType type = (counter % 2 == 0) ? TileType.WALL : TileType.SIMPLE;
                tiles.add(new TileImpl(new Vector2D(i, j), new Vector2D(i * TILE_SIZE, j * TILE_SIZE), null, type));
                counter++;
            }
        }

        Map gameMap = new MapImpl(tiles);

        Tile pacmanSpawn = tiles.stream()
                .filter(tile -> tile.getMatrixPosition().equals(new Vector2D(0, 1)))
                .findFirst().orElseThrow();

        Tile ghostSpawn = tiles.stream()
                .filter(tile -> tile.getMatrixPosition().equals(new Vector2D(1, 0)))
                .findFirst().orElseThrow();

        Set<Pacman> pacmans = new HashSet<>();
        pacmans.add(new PacmanImpl(pacmanSpawn));

        Set<Ghost> ghosts = new HashSet<>();
        ghosts.add(new GhostImpl(ghostSpawn.getCenterPosition()));

        GameContext factory = new GameContextImpl(gameMap, dots, ghosts, pacmans, Duration.of(GAME_DURATION_SECONDS, TimeUnit.SECONDS.toChronoUnit()));
        factory.createGameState();
        return factory;
    }
}
