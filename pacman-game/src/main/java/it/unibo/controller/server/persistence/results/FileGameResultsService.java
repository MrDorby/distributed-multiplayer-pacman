package it.unibo.controller.server.persistence.results;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class FileGameResultsService implements GameResultsService {
    private static final Logger logger = LoggerFactory.getLogger(FileGameResultsService.class);
    private final ObjectMapper mapper;

    public FileGameResultsService() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public CompletableFuture<?>[] saveResults(MatchSnapshot snapshot) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            String fileName = String.format("result_match_%s_%d.json", snapshot.matchId(), snapshot.timestamp());
            File destinationFile = new File(fileName);
            try {
                mapper.writeValue(destinationFile, snapshot);
                logger.info("Saved final game results to {}", destinationFile.getAbsolutePath());
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