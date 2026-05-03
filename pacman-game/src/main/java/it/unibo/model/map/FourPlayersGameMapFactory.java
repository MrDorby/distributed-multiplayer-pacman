package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static it.unibo.model.common.GameConstants.TILE_SIZE;

/**
 * A Factory that creates FourPlayersGameMaps.
 */
public class FourPlayersGameMapFactory implements GameMapFactory {
    @Override
    public GameMap fromJSON(String path) {
        try {
            InputStream JSONStream = ClassLoader.getSystemResourceAsStream(path);
            GameMapJSON gameMapJSON = new ObjectMapper().readValue(JSONStream, new TypeReference<GameMapJSON>() {});
            if (gameMapJSON.rows * gameMapJSON.columns == gameMapJSON.tiles.size()) {
                Map<MatrixCoordinates, Tile> tilesMap = new HashMap<>();
                IntStream.range(0, gameMapJSON.rows).forEach(i ->
                        IntStream.range(0, gameMapJSON.columns).forEach(j -> {
                            String tileChar = gameMapJSON.tiles.get(gameMapJSON.columns * i + j);
                            MatrixCoordinates tileCoordinate = new MatrixCoordinates(i, j);
                            Optional<Tile> tile = createTile(tileCoordinate, tileChar);
                            if (tile.isEmpty()) {
                                throw new IllegalArgumentException(
                                        "Invalid map character \"" + tileChar + "\" in JSON file: " + path);
                            }
                            tilesMap.put(tileCoordinate, tile.get());
                        }));
                return new FourPlayersGameMap(tilesMap, new MatrixCoordinates(gameMapJSON.rows, gameMapJSON.columns));
            } else {
                throw new IllegalArgumentException(
                    "The number of tiles does not correspond to the specified map size (rows * columns) in JSON file: "
                            + path);
            }
        } catch (JacksonException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Optional<Tile> createTile(MatrixCoordinates coordinates, String typeString) {
        Vector2D tileCenterPosition = new Vector2D(
                computeCenterPosition(coordinates.row()),
                computeCenterPosition(coordinates.column()));
        // TODO: refactor
        return switch (typeString) {
            case "E" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.SIMPLE));
            case "D" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.SIMPLE)); // TODO: create a Dot here
            case "S" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.SIMPLE)); // TODO: create a special Dot here
            case "W" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.WALL));
            case "G" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.GHOST_SPAWN));
            case "P" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.PACMAN_SPAWN));
            default -> Optional.empty();
        };
    }

    private int computeCenterPosition(int coordinate) {
        return (coordinate * TILE_SIZE) + (TILE_SIZE / 2);
    }

    private record GameMapJSON(int rows, int columns, List<String> tiles) {}
}
