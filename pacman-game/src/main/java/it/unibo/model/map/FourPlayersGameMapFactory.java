package it.unibo.model.map;

import java.util.Optional;

// TODO: write documentation about the type of GameMap that this Factory creates
public class FourPlayersGameMapFactory implements GameMapFactory {
    @Override
    public Optional<GameMap> fromJSON(String path) {
        return Optional.empty();
    }
}
