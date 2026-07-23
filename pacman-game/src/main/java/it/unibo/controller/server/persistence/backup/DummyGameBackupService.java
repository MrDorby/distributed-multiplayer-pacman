package it.unibo.controller.server.persistence.backup;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class DummyGameBackupService implements GameBackupService {
    private static final Logger logger = LoggerFactory.getLogger(DummyGameBackupService.class);

    @Override
    public CompletableFuture<Void> saveSnapshot(MatchSnapshot snapshot) {
        logger.info("Saving snapshot taken at {} with matchId: {}", snapshot.timestamp(), snapshot.matchId());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void closeConnection() {}
}
