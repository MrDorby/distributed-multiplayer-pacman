package it.unibo.controller.server.persistence.backup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * HTTP-based implementation of {@link GameBackupService} that POSTs the game context
 * as JSON to a configured backup endpoint.
 */
public class HttpGameBackupService implements GameBackupService {
    private final Logger logger = LoggerFactory.getLogger(HttpGameBackupService.class);

    private final HttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final URI endpoint;

    /**
     * @param endpoint the URI of the backup backend to POST snapshots to
     */
    public HttpGameBackupService(HttpClient client, URI endpoint) {
        this.client = client;
        this.endpoint = endpoint;
    }

    @Override
    public CompletableFuture<Void> saveSnapshot(GameContextDTO dto) {
        try {
            byte[] body = objectMapper.writeValueAsBytes(dto);
            HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> {
                        if (response.statusCode() < 200 || response.statusCode() >= 300) {
                            throw new CompletionException(new IOException("Unexpected status code: " + response.statusCode()));
                        }
                        logger.info("Snapshot saved, status code: {}", response.statusCode());
                    });
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}