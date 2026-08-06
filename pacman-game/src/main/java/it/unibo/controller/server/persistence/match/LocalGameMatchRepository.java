package it.unibo.controller.server.persistence.match;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * In-memory mock implementation of {@link GameMatchRepository} for local testing.
 * <p>
 * Always returns a standard static four-player whitelist regardless of the requested match ID.
 */
public class LocalGameMatchRepository implements GameMatchRepository {
    private static final List<String> DEFAULT_LOCAL_PLAYERS = List.of("player1", "player2", "player3", "player4");

    @Override
    public CompletableFuture<Optional<List<String>>> findExpectedPlayers(String matchId) {
        return CompletableFuture.completedFuture(Optional.of(DEFAULT_LOCAL_PLAYERS));
    }

    @Override
    public void close() {}
}