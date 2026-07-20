package it.unibo.controller.server.persistence.results;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.mongodb.MongoDBConstants;
import it.unibo.controller.server.persistence.mongodb.MongoDBServerConnection;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP-based implementation of {@link GameResultsService} that publishes game results
 * to a long-term results backend via a JSON POST request.
 */
public class HttpGameResultsService implements GameResultsService {

    private final MongoDBServerConnection mongoDBServerConnection;

    public HttpGameResultsService() {
        this.mongoDBServerConnection = new MongoDBServerConnection(MongoDBConstants.ConnectToDatabase.LONG_TERM);
    }

    @Override
    public CompletableFuture<?>[] saveResults(MatchSnapshot snapshot) {
        return this.mongoDBServerConnection.saveResultsOnLongTermDB(snapshot);
    }

    @Override
    public void closeConnection() {
        this.mongoDBServerConnection.closeConnection();
    }
}
