package it.unibo.controller.server.persistence.results;

import it.unibo.controller.shared.network.dto.GameContextDTO;

import java.util.concurrent.CompletableFuture;

/**
 * Saves aggregated game results in the long-term results backend once a game has ended.
 */
public interface GameResultsService {
    /**
     * Saves the aggregated results derived from the given game context.
     */
    CompletableFuture<Void> saveResults(GameContextDTO dto);
}
