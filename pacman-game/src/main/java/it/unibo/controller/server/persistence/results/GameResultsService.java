package it.unibo.controller.server.persistence.results;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;

import java.util.concurrent.CompletableFuture;

/**
 * Saves aggregated game results in the long-term results backend once a game has ended.
 */
public interface GameResultsService {
    /**
     * Saves the aggregated results derived from the given game context.
     */
    CompletableFuture<?>[] saveResults(MatchSnapshot snapshot);

    /**
     * Closes the MongoDB connection (MongoClient) and frees the resources.
     */
    void closeConnection();
}
