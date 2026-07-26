package it.unibo.controller.server.persistence.backup;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Backs up the full game state to a short-term backup store, for recovery purposes.
 */
public interface GameSnapshotRepository extends AutoCloseable {
    /**
     * Saves a snapshot of the game context to the backup store.
     */
    CompletableFuture<Void> saveSnapshot(MatchSnapshot snapshot);

    CompletableFuture<Optional<MatchSnapshot>> findLatestSnapshot(String matchId);
}
