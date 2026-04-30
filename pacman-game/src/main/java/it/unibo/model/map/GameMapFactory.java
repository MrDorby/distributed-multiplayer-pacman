package it.unibo.model.map;

/**
 * A factory for instantiating a Map.
 */
public interface GameMapFactory {
    /**
     * Creates a Map based on a JSON file.</br>
     * The file must have the following structure:
     * <ul>
     *     <li>A {@code rows} field specifying the number of rows in the Map grid</li>
     *     <li>A {@code columns} field specifying the number of columns in the Map grid</li>
     *     <li>A {@code tiles} field with the list of Tiles (which must be of size {@code height * width})</li>
     * </ul>
     * Each Tile is represented by a character, according to its type:
     * <ul>
     *     <li>{@code E} An empty Tile.</li>
     *     <li>{@code D} A Tile containing a Dot.</li>
     *     <li>{@code W} A wall Tile.</li>
     *     <li>{@code G} The ghosts' spawn point.</li>
     *     <li>{@code P} A Pacman's spawn point.</li>
     * </ul>
     * @param path the path of the JSON file to read.
     * @return a Map with the given configuration.
     */
    GameMap fromJSON(String path);
}
