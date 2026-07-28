package it.unibo.controller.server.persistence.snapshot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Local development implementation of {@link GameSnapshotRepository}.
 * Reads match snapshots directly from the local {@code .temp/matches} directory.
 */
public class LocalGameSnapshotRepository implements GameSnapshotRepository {
    private static final Logger logger = LoggerFactory.getLogger(LocalGameSnapshotRepository.class);
    private final ObjectMapper mapper;

    public LocalGameSnapshotRepository() {
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
                throw new RuntimeException("Failed to persist snapshot for matchId: " + snapshot.matchId(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<MatchSnapshot>> findLatestSnapshot(String matchId) {
        return CompletableFuture.supplyAsync(() -> {
            Path snapshotDirectory = Paths.get(".temp", "matches", matchId, "snapshots");
            File directory = snapshotDirectory.toFile();
            if (!directory.exists() || !directory.isDirectory()) {
                logger.warn("No snapshot directory found for matchId: {}", matchId);
                return Optional.empty();
            }
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null || files.length == 0) {
                logger.warn("No snapshot files found for matchId: {}", matchId);
                return Optional.empty();
            }
            Optional<File> latestFileOpt = Arrays.stream(files).max(Comparator.comparingLong(this::extractTimestamp));
            File latestFile = latestFileOpt.get();
            try {
                logger.info("Restoring latest snapshot from {}", latestFile.getAbsolutePath());
                MatchSnapshot snapshot = mapper.readValue(latestFile, MatchSnapshot.class);
                return Optional.ofNullable(snapshot);
            } catch (IOException e) {
                logger.error("Failed to read snapshot file: {}", latestFile.getName(), e);
                throw new RuntimeException("Failed to deserialize snapshot from " + latestFile.getName(), e);
            }
        });
    }

    private long extractTimestamp(File file) {
        String name = file.getName();
        try {
            return Long.parseLong(name.substring(0, name.lastIndexOf('.')));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public void close() throws Exception {}
}