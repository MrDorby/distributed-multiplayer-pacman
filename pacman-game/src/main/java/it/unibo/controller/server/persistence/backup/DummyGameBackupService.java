package it.unibo.controller.server.persistence.backup;

import it.unibo.controller.shared.network.dto.GameContextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class DummyGameBackupService implements GameBackupService {
    private static final Logger logger = LoggerFactory.getLogger(DummyGameBackupService.class);

    @Override
    public CompletableFuture<Void> saveSnapshot(GameContextDTO dto) {
        logger.info("Saving snapshot.");
        return CompletableFuture.completedFuture(null);
    }
}
