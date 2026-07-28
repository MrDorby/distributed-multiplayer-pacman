package it.unibo.controller.server.persistence.snapshot;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Backs up the game state to a short-term database, for recovery purposes.
 */
public interface GameSnapshotRepository extends AutoCloseable {

    /**
     * Saves a snapshot of the game context to the backup store.
     */
    CompletableFuture<Void> saveSnapshot(MatchSnapshot snapshot);

    /**
     * Retrieves the most recent saved snapshot for a given match ID.
     *
     * @param matchId the unique identifier of the match
     * @return a {@link CompletableFuture} containing an {@link Optional} with the latest {@link MatchSnapshot},
     *         or {@link Optional#empty()} if no snapshot exists for the specified match
     */
    CompletableFuture<Optional<MatchSnapshot>> findLatestSnapshot(String matchId);
}
