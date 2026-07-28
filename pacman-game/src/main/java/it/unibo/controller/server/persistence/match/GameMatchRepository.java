package it.unibo.controller.server.persistence.match;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Data access repository for retrieving match related information such as the expected players list.
 */
public interface GameMatchRepository extends AutoCloseable {
    /**
     * Retrieves the expected players list for a specified match.
     *
     * @param matchId the unique identifier of the match
     * @return a {@link CompletableFuture} containing an {@link Optional} list of player usernames.
     */
    CompletableFuture<Optional<List<String>>> findExpectedPlayers(String matchId);
}
