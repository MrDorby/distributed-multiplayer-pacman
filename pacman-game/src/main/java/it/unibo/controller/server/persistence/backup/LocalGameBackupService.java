package it.unibo.controller.server.persistence.backup;

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
 * Local development implementation of {@link GameBackupService}.
 * Writes snapshots to the local {@code .temp/matches} folder for easy debugging and testing.
 */
public class LocalGameBackupService implements GameBackupService {
    private static final Logger logger = LoggerFactory.getLogger(LocalGameBackupService.class);
    private final ObjectMapper mapper;

    public LocalGameBackupService() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public CompletableFuture<Void> saveSnapshot(MatchSnapshot snapshot) {
        return CompletableFuture.runAsync(() -> {
            Path snapshotDirectory = Paths.get(".temp", "matches", snapshot.matchId(), "snapshots");
            Path targetFile = snapshotDirectory.resolve(snapshot.timestamp() + ".json");
            try {
                Files.createDirectories(snapshotDirectory);
                mapper.writeValue(targetFile.toFile(), snapshot);
                logger.info("Saved snapshot to {}", targetFile.toAbsolutePath());
            } catch (IOException e) {
                logger.error("Failed to save snapshot for matchId: {}", snapshot.matchId(), e);
                throw new RuntimeException("Failed to persist snapshot", e);
            }
        });
    }

    @Override
    public void closeConnection() {}
}