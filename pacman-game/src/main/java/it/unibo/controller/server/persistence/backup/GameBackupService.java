package it.unibo.controller.server.persistence.backup;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;

import java.util.concurrent.CompletableFuture;

/**
 * Backs up the full game state to a short-term backup store, for recovery purposes.
 */
public interface GameBackupService {
    /**
     * Saves a snapshot of the game context to the backup store.
     */
    CompletableFuture<?> saveSnapshot(MatchSnapshot snapshot);

    /**
     * Closes the MongoDB connection (MongoClient) and frees the resources.
     */
    void closeConnection();
}
