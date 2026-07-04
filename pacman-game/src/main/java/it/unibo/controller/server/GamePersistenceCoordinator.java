package it.unibo.controller.server;

import it.unibo.controller.server.backup.GameBackupService;
import it.unibo.controller.server.results.GameResultsService;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.translation.GameContextEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class GameContextPersistenceController {
    private static final Logger logger = LoggerFactory.getLogger(GameContextPersistenceController.class.getName());
    private static final long PERIOD_SECONDS = 10;

    private final GameBackupService backupService;
    private final GameResultsService resultsService;
    private final GameContextEncoder encoder;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile GameContextDTO lastContext;
    private ScheduledFuture<?> periodicTask;

    public GamePersistenceCoordinator(GameBackupService backupService,
                                      GameResultsService resultsService,
                                      GameContextEncoder encoder) {
        this.backupService = backupService;
        this.resultsService = resultsService;
        this.encoder = encoder;
    }

    public void updateContext(GameContextDTO context) {
        this.lastContext = context;
    }

    public void start() {
        periodicTask = scheduler.scheduleAtFixedRate(
                this::saveLatestIfPresent, PERIOD_SECONDS, PERIOD_SECONDS, TimeUnit.SECONDS);
        logger.info("Periodic context persistence started (every {}s).", PERIOD_SECONDS);
    }

    private void saveLatestIfPresent() {
        GameContextDTO dto = lastContext;
        if (dto == null) return;
        try {
            persister.saveSnapshot(dto);
        } catch (Exception e) {
            logger.warn("Periodic save threw unexpectedly", e);
        }
    }

    public void onGameEnded(GameContextDTO finalContext) {
        if (periodicTask != null) {
            periodicTask.cancel(false);
        }
        try {
            persister.saveSnapshot(finalContext).get(5, TimeUnit.SECONDS);
            logger.info("Final game context persisted.");
        } catch (Exception e) {
            logger.error("Final context save failed or timed out", e);
        }
        shutdown();
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
        }
    }
}
