package it.unibo.controller.server.persistence.results;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * Local development implementation of {@link GameResultsService}.
 * Writes match results to the local {@code .temp/matches} directory.
 */
public class LocalGameResultsService implements GameResultsService {
    private static final Logger logger = LoggerFactory.getLogger(LocalGameResultsService.class);
    private final ObjectMapper mapper;

    public LocalGameResultsService() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public CompletableFuture<?>[] saveResults(MatchSnapshot snapshot) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            Path matchDirectory = Paths.get(".temp", "matches", snapshot.matchId());
            Path targetFile = matchDirectory.resolve("result.json");
            try {
                Files.createDirectories(matchDirectory);
                mapper.writeValue(targetFile.toFile(), snapshot);
                logger.info("Saved game results to {}", targetFile.toAbsolutePath());
            } catch (IOException e) {
                logger.error("Failed to save game results for matchId: {}", snapshot.matchId(), e);
                throw new RuntimeException("Failed to persist game results", e);
            }
        });
        return new CompletableFuture<?>[]{ future };
    }

    @Override
    public void closeConnection() {}
}