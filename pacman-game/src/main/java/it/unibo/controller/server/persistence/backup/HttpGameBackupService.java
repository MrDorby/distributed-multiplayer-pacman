package it.unibo.controller.server.persistence.backup;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.mongodb.MongoDBConstants;
import it.unibo.controller.server.persistence.mongodb.MongoDBServerConnection;

import java.util.concurrent.CompletableFuture;

/**
 * HTTP-based implementation of {@link GameBackupService} that POSTs the game snapshot
 * as JSON to a configured backup endpoint.
 */
public class HttpGameBackupService implements GameBackupService {

    private final MongoDBServerConnection mongoDBServerConnection;

    public HttpGameBackupService() {
        this.mongoDBServerConnection = new MongoDBServerConnection(MongoDBConstants.ConnectToDatabase.SHORT_TERM);
    }

    @Override
    public CompletableFuture<?> saveSnapshot(MatchSnapshot snapshot) {
        return this.mongoDBServerConnection.saveResultsOnShortTermDB(snapshot);
    }

    @Override
    public void closeConnection() {
        this.mongoDBServerConnection.closeConnection();
    }
}