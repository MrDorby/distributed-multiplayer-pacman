package it.unibo.controller.server.persistence.backup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class FileGameBackupService implements GameBackupService {
    private static final Logger logger = LoggerFactory.getLogger(FileGameBackupService.class);
    private final ObjectMapper mapper;

    public FileGameBackupService() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public CompletableFuture<Void> saveSnapshot(MatchSnapshot snapshot) {
        return CompletableFuture.runAsync(() -> {
            String fileName = String.format("match_%s_%d.json", snapshot.matchId(), snapshot.timestamp());
            File destinationFile = new File(fileName);
            try {
                mapper.writeValue(destinationFile, snapshot);
                logger.info("Saved snapshot to {}", destinationFile.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Failed to save snapshot for matchId: {}", snapshot.matchId(), e);
                throw new RuntimeException("Failed to persist snapshot", e);
            }
        });
    }

    @Override
    public void closeConnection() {}
}