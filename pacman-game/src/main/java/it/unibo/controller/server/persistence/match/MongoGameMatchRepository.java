package it.unibo.controller.server.persistence.match;

import it.unibo.controller.server.persistence.mongodb.MongoDBConstants;
import it.unibo.controller.server.persistence.mongodb.MongoDBServerConnection;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * MongoDB-backed implementation of {@link GameMatchRepository}.
 */
public class MongoGameMatchRepository implements GameMatchRepository {
    private final MongoDBServerConnection connection;

    public MongoGameMatchRepository(URI databaseURI) {
        this.connection = new MongoDBServerConnection(MongoDBConstants.ConnectToDatabase.SHORT_TERM, databaseURI);
    }

    @Override
    public CompletableFuture<Optional<List<String>>> findExpectedPlayers(String matchId) {
        return connection.retrievePlayers(matchId);
    }

    @Override
    public void close() throws Exception {
        this.connection.closeConnection();
    }
}
