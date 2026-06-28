package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Paths;
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
            GameMapJSON gameMapJSON = new ObjectMapper().readValue(JSONStream, new TypeReference<>() {});
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
                String mapName = Paths.get(path).getFileName().toString().replaceFirst("\\.json$", "");
                return new FourPlayersGameMap(mapName, tilesMap, new MatrixCoordinates(gameMapJSON.rows, gameMapJSON.columns));
            } else {
                throw new IllegalArgumentException(
                    "The number of tiles does not correspond to the specified map size (rows * columns) in JSON file: "
                            + path);
            }
        } catch (JacksonException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Instantiate a Tile in the given GameMap coordinates based on the given type string.
     * @param coordinates the coordinates of the Tile in the GameMap.
     * @param typeString the string that represents the Tile's type.
     * @return the created Tile, if present. An empty optional will be returned in case the passed {@code typeString}
     *      is invalid.
     */
    private Optional<Tile> createTile(MatrixCoordinates coordinates, String typeString) {
        Vector2D tileCenterPosition = new Vector2D(
                computeCenterPosition(coordinates.column()),
                computeCenterPosition(coordinates.row()));
        Optional<TileType> tileType = switch (typeString) {
            case "E" -> Optional.of(TileType.EMPTY);
            case "D" -> Optional.of(TileType.DOT);
            case "S" -> Optional.of(TileType.SPECIAL_DOT);
            case "W" -> Optional.of(TileType.WALL);
            case "G" -> Optional.of(TileType.GHOST_SPAWN);
            case "P" -> Optional.of(TileType.PACMAN_SPAWN);
            default -> Optional.empty();
        };
        return tileType.map(type -> new TileImpl(coordinates, tileCenterPosition, type));
    }

    /**
     * Calculates the center position component of a Tile, based on its coordinate.
     * @param coordinate the coordinate.
     * @return the position component.
     */
    private int computeCenterPosition(int coordinate) {
        return (coordinate * TILE_SIZE) + (TILE_SIZE / 2);
    }

    /**
     * A representation of the GameMap's JSON file.
     * @param rows the number of rows.
     * @param columns the number of columns.
     * @param tiles the array containing the type strings of each Tile in the map.
     */
    private record GameMapJSON(int rows, int columns, List<String> tiles) {}
}
