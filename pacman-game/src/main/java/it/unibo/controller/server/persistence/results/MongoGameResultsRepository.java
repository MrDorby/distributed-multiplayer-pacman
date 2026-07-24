package it.unibo.controller.server.persistence.results;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.mongodb.MongoDBConstants;
import it.unibo.controller.server.persistence.mongodb.MongoDBServerConnection;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class MongoGameResultsRepository implements GameResultsRepository {

    private final MongoDBServerConnection connection;

    public MongoGameResultsRepository(URI resultsRepositoryURI) {
        this.connection = new MongoDBServerConnection(MongoDBConstants.ConnectToDatabase.LONG_TERM, resultsRepositoryURI);
    }

    @Override
    public CompletableFuture<Void> saveResults(MatchSnapshot snapshot) {
        return this.connection.saveResultsOnLongTermDB(snapshot);
    }

    @Override
    public void close() {
        this.connection.closeConnection();
    }
}
