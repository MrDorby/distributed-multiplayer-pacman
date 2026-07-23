package it.unibo.controller.server.persistence.backup;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Local development implementation of {@link GameSnapshotRepository}.
 * Reads match snapshots directly from the local {@code .temp/matches} directory.
 */
public class LocalGameSnapshotRepository implements GameSnapshotRepository {
    private static final Logger logger = LoggerFactory.getLogger(LocalGameSnapshotRepository.class);
    private final ObjectMapper mapper;

    public LocalGameSnapshotRepository() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public Optional<MatchSnapshot> findLatestSnapshot(String matchId) {
        Path snapshotDirectory = Paths.get(".temp", "matches", matchId, "snapshots");
        File directory = snapshotDirectory.toFile();
        if (!directory.exists() || !directory.isDirectory()) {
            logger.warn("No snapshot directory found for matchId: {}", matchId);
            return Optional.empty();
        }
        File[] files = directory.listFiles((_, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            logger.warn("No snapshot files found for matchId: {}", matchId);
            return Optional.empty();
        }
        File latestFile = Arrays.stream(files)
                .max(Comparator.comparingLong(this::extractTimestamp))
                .orElse(null);
        try {
            logger.info("Restoring latest snapshot from {}", latestFile.getAbsolutePath());
            MatchSnapshot snapshot = mapper.readValue(latestFile, MatchSnapshot.class);
            return Optional.of(snapshot);
        } catch (IOException e) {
            logger.error("Failed to read snapshot file: {}", latestFile.getName(), e);
            return Optional.empty();
        }
    }

    private long extractTimestamp(File file) {
        String name = file.getName();
        try {
            return Long.parseLong(name.substring(0, name.lastIndexOf('.')));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}