package it.unibo.model.game;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.*;
import it.unibo.model.map.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static it.unibo.model.common.GameConstants.GAME_DURATION_SECONDS;
import static it.unibo.model.common.GameConstants.TILE_SIZE;

public class GameContextFactory {
    public static GameContext getTestContext() {

        Set<Tile> tiles = new HashSet<>();
        Map<MatrixCoordinates, Dot> dotsMap = new HashMap<>();

        int counter = 0;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                TileType type = (counter % 2 == 0) ? TileType.WALL : TileType.SIMPLE;
                if (i == 0 && j > 0 && j < 5) {
                    type = TileType.PACMAN_SPAWN;
                }
                if (i == 1 && j == 0) {
                    type = TileType.GHOST_SPAWN;
                }
                tiles.add(new TileImpl(new MatrixCoordinates(i, j), new Vector2D(i * TILE_SIZE, j * TILE_SIZE), Optional.empty(), type));
                counter++;
            }
        }

        Map<MatrixCoordinates, Tile> tilesMap = new HashMap<>();
        tiles.forEach(t -> tilesMap.put(t.getMatrixPosition(), t));
        GameMap gameMap = new FourPlayersGameMap(tilesMap, new MatrixCoordinates(16, 16));

        Tile pacmanSpawn = tiles.stream()
                .filter(tile -> tile.getMatrixPosition().equals(new MatrixCoordinates(0, 1)))
                .findFirst().orElseThrow();

        Tile pacmanSpawn2 = tiles.stream()
                .filter(tile -> tile.getMatrixPosition().equals(new MatrixCoordinates(0, 2)))
                .findFirst().orElseThrow();

        Tile ghostSpawn = tiles.stream()
                .filter(tile -> tile.getMatrixPosition().equals(new MatrixCoordinates(1, 0)))
                .findFirst().orElseThrow();

        Set<Pacman> pacmans = new HashSet<>();
        Pacman pacman1 = new PacmanImpl(pacmanSpawn, gameMap);
        Pacman pacman2 = new PacmanImpl(pacmanSpawn2, gameMap);
        pacmans.add(pacman1);
        pacmans.add(pacman2);

        Set<Ghost> ghosts = new HashSet<>();
        ghosts.add(new GhostImpl(ghostSpawn, gameMap));

        return new GameContextImpl(gameMap, dotsMap, ghosts, pacmans, Duration.of(GAME_DURATION_SECONDS, TimeUnit.SECONDS.toChronoUnit()));
    }

    public static GameContext createFromMap(GameMap gameMap) {
        Map<MatrixCoordinates, Dot> dotsMap = new HashMap<>();
        Set<Ghost> ghosts = new HashSet<>();
        Set<Pacman> pacmans = new HashSet<>();
        gameMap.getTiles().forEach(tile -> {
            if (tile.getDot().isPresent()) {
                dotsMap.put(tile.getMatrixPosition(), tile.getDot().get());
            } else if (tile.getTileType() == TileType.PACMAN_SPAWN) {
                Pacman pacman = new PacmanImpl(tile, gameMap);
                pacmans.add(pacman);
            } else if (tile.getTileType() == TileType.GHOST_SPAWN) {
                Ghost ghost = new GhostImpl(tile, gameMap);
                ghosts.add(ghost);
            }
        });
        return new GameContextImpl(
                gameMap,
                dotsMap,
                ghosts,
                pacmans,
                Duration.of(GAME_DURATION_SECONDS, TimeUnit.SECONDS.toChronoUnit())
        );
    }
}
