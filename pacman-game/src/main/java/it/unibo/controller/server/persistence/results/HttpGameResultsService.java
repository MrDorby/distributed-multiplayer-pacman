package it.unibo.controller.server.persistence.results;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.controller.server.persistence.mongodb.ConnectToDatabase;
import it.unibo.controller.server.persistence.mongodb.MongoDBServerConnection;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * HTTP-based implementation of {@link GameResultsService} that publishes game results
 * to a long-term results backend via a JSON POST request.
 */
public class HttpGameResultsService implements GameResultsService {

    // TODO: REMOVE UNUSED ELEMENTS
    private static final Logger logger = LoggerFactory.getLogger(HttpGameResultsService.class);
    private final HttpClient client;
    private final MongoDBServerConnection mongoDBServerConnection;
    private final ObjectMapper mapper = new ObjectMapper();
    private final URI endpoint;

    /**
     * @param endpoint the URI of the results backend to POST results to.
     */
    public HttpGameResultsService(HttpClient client, URI endpoint) {
        this.client = client;
        this.mongoDBServerConnection = new MongoDBServerConnection(ConnectToDatabase.LONG_TERM);
        this.endpoint = endpoint;
    }

    @Override
    public CompletableFuture<Void> saveResults(GameContextDTO dto) {
        CompletableFuture<Void> future = this.mongoDBServerConnection.saveResultsOnDB(dto);
        this.mongoDBServerConnection.closeConnection();
        return future;
        // try {
        //     byte[] body = mapper.writeValueAsBytes(dto);
        //     HttpRequest request = HttpRequest.newBuilder(endpoint)
        //             .header("Content-Type", "application/json")
        //             .POST(HttpRequest.BodyPublishers.ofByteArray(body))
        //             .build();
        //     return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
        //             .thenAccept(response -> {
        //                 if (response.statusCode() < 200 || response.statusCode() >= 300) {
        //                     throw new CompletionException(new IOException("Unexpected status code: " + response.statusCode()));
        //                 }
        //                 logger.info("Results saved, status code: {}", response.statusCode());
        //             });
        // } catch (JsonProcessingException e) {
        //     return CompletableFuture.failedFuture(e);
        // }
    }
}
