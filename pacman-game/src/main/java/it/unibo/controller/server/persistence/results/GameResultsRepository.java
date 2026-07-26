package it.unibo.controller.server.persistence.results;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;

import java.util.concurrent.CompletableFuture;

/**
 * Saves aggregated game results in the long-term results database once a game has ended.
 */
public interface GameResultsRepository extends AutoCloseable {
    /**
     * Saves aggregated results derived from the given game snapshot.
     */
    CompletableFuture<Void> saveResults(MatchSnapshot snapshot);
}
