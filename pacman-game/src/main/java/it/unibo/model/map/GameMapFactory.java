package it.unibo.model.map;

import java.util.Optional;

/**
 * A factory for instantiating a Map.
 */
public interface GameMapFactory {
    /**
     * Creates a GameMap based on a JSON file.</br>
     * The file must have the following structure:
     * <ul>
     *     <li>A {@code rows} field specifying the number of rows in the GameMap grid</li>
     *     <li>A {@code columns} field specifying the number of columns in the GameMap grid</li>
     *     <li>A {@code tiles} field with the list of Tiles (which must be of size {@code height * width})</li>
     * </ul>
     * Each Tile is represented by a character, according to its type:
     * <ul>
     *     <li>{@code E} An empty Tile.</li>
     *     <li>{@code D} A Tile containing a Dot.</li>
     *     <li>{@code S} A Tile containing a Special Dot.</li>
     *     <li>{@code W} A wall Tile.</li>
     *     <li>{@code G} The ghosts' spawn point.</li>
     *     <li>{@code P} A Pacman's spawn point.</li>
     * </ul>
     * TODO: put the following check directly in the map. This class's responsibility is just to create a Map with
     *  the characteristics that are specified in the JSON file.
     * Also, a valid map must have exactly one ghost spawn point and four Pacman spawn points.
     * @param path the path of the JSON file to read.
     * @return an Optional containing a GameMap if the JSON is valid, an empty Optional otherwise.
     * The file is not considered valid if it does not adhere to the specification (missing fields,
     * invalid map characters, wrong number of tiles in the grid, etc.).
     */
    Optional<GameMap> fromJSON(String path);
}
