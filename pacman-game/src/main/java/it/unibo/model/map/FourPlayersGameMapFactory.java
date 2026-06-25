package it.unibo.model.map;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.GameEntityFactory;
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
    private final GameEntityFactory gameEntityFactory;

    /**
     * @param gameEntityFactory the factory that will be used to instantiate the Dots in the GameMap.
     */
    public FourPlayersGameMapFactory(final GameEntityFactory gameEntityFactory) {
        this.gameEntityFactory = gameEntityFactory;
    }

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
        return switch (typeString) {
            // TODO: Modify instantiation logic of Tiles
            case "E" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.SIMPLE));
            case "D" -> Optional.of(new TileImpl(coordinates, tileCenterPosition,
                    Optional.of(this.gameEntityFactory.createDot(tileCenterPosition, false)), TileType.SIMPLE));
            case "S" -> Optional.of(new TileImpl(coordinates, tileCenterPosition,
                    Optional.of(this.gameEntityFactory.createDot(tileCenterPosition, true)), TileType.SIMPLE));
            case "W" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.WALL));
            case "G" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.GHOST_SPAWN));
            case "P" -> Optional.of(new TileImpl(coordinates, tileCenterPosition, Optional.empty(), TileType.PACMAN_SPAWN));
            default -> Optional.empty();
        };
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
