package it.unibo.controller.network.translation;

import it.unibo.controller.network.dto.GameContextDTO;
import it.unibo.model.game.GameContext;

/**
 * Encodes a {@link GameContext} into a {@link GameContextDTO}.
 */
public interface GameContextEncoder {
    /**
     * Converts a {@link GameContext} to its DTO representation.
     *
     * @param context the game context to convert
     * @return the corresponding {@link GameContextDTO}
     */
    GameContextDTO encode(GameContext context);
}
