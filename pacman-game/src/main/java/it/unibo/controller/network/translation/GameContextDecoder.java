package it.unibo.controller.network.translation;

import it.unibo.controller.network.dto.GameContextDTO;
import it.unibo.model.game.GameContext;

/**
 * Decodes a {@link GameContextDTO} into a {@link GameContext}.
 */
public interface GameContextDecoder {
    /**
     * Converts a {@link GameContextDTO} to its domain representation.
     *
     * @param dto the DTO to decode
     * @return the reconstructed {@link GameContext}
     */
    GameContext decode(GameContextDTO dto);
}
