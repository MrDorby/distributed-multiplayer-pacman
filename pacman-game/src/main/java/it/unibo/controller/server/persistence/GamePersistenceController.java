package it.unibo.controller.server.persistence;

import it.unibo.controller.server.persistence.backup.GameBackupService;
import it.unibo.controller.server.persistence.results.GameResultsService;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Coordinates periodic and end-of-game persistence of game state.
 * <p>
 * While a game is running, periodically backs up the most recently supplied game
 * context via {@link GameBackupService}. Once the game ends, cancels the periodic
 * schedule and concurrently saves a final backup and the game's results via
 * {@link GameResultsService}.
 */
public class GamePersistenceController {
    private static final Logger logger = LoggerFactory.getLogger(GamePersistenceController.class);
    private static final long PERIOD_IN_SECONDS = 10;

    private final GameBackupService backupService;
    private final GameResultsService resultsService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile GameContextDTO lastContext;
    private ScheduledFuture<?> periodicTask;

    public GamePersistenceController(GameBackupService backupService, GameResultsService resultsService) {
        this.backupService = backupService;
        this.resultsService = resultsService;
    }

    public void updateContext(GameContextDTO context) {
        this.lastContext = context;
    }

    public void start() {
        periodicTask = scheduler.scheduleAtFixedRate(this::saveSnapShot, PERIOD_IN_SECONDS, PERIOD_IN_SECONDS, TimeUnit.SECONDS);
        logger.info("Game context save routine started (every {}s).", PERIOD_IN_SECONDS);
    }

    private void saveSnapShot() {
        GameContextDTO dto = lastContext;
        if (dto == null) return;
        backupService.saveSnapshot(dto)
                .exceptionally(ex -> {
                    logger.warn("Periodic snapshot failed", ex);
                    return null;
                });
    }

    public void onGameEnded(GameContextDTO dto) {
        if (periodicTask != null) {
            periodicTask.cancel(false);
        }
        CompletableFuture<Void> backupSaved = backupService.saveSnapshot(dto);
        CompletableFuture<Void> resultsSaved = resultsService.saveResults(dto);
        try {
            CompletableFuture.allOf(backupSaved, resultsSaved).get(5, TimeUnit.SECONDS);
            logger.info("Final game context and results persisted.");
        } catch (Exception e) {
            logger.error("Final backup and/or results save failed or timed out", e);
        }
        stop();
    }

    public void stop() {
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
