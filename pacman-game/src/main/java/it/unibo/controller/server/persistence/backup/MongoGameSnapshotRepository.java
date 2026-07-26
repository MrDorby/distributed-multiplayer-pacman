package it.unibo.controller.server.persistence.backup;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.mongodb.MongoDBConstants;
import it.unibo.controller.server.persistence.mongodb.MongoDBServerConnection;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP-based implementation of {@link GameSnapshotRepository} that POSTs the game snapshot
 * as JSON to a configured backup endpoint.
 */
public class MongoGameSnapshotRepository implements GameSnapshotRepository {

    private final MongoDBServerConnection connection;

    public MongoGameSnapshotRepository(URI backupRepositoryURI) {
        this.connection = new MongoDBServerConnection(MongoDBConstants.ConnectToDatabase.SHORT_TERM, backupRepositoryURI);
    }

    @Override
    public CompletableFuture<Void> saveSnapshot(MatchSnapshot snapshot) {
        return this.connection.saveResultsOnShortTermDB(snapshot);
    }

    @Override
    public CompletableFuture<Optional<MatchSnapshot>> findLatestSnapshot(String matchId) {
        return this.connection.getCheckpoint(matchId);
    }

    @Override
    public void close() {
        this.connection.closeConnection();
    }
}