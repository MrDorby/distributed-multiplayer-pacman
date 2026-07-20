package it.unibo.controller.server.persistence;

import it.unibo.controller.server.persistence.backup.GameBackupService;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.results.GameResultsService;
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
public class GamePersistenceManager {
    private static final Logger logger = LoggerFactory.getLogger(GamePersistenceManager.class);
    private static final long PERIOD_IN_SECONDS = 10;

    private final GameBackupService backupService;
    private final GameResultsService resultsService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile MatchSnapshot currentSnapshot;
    private ScheduledFuture<?> periodicTask;

    public GamePersistenceManager(GameBackupService backupService, GameResultsService resultsService) {
        this.backupService = backupService;
        this.resultsService = resultsService;
    }

    public void updateContext(MatchSnapshot snapshot) {
        this.currentSnapshot = snapshot;
    }

    public void start() {
        periodicTask = scheduler.scheduleAtFixedRate(this::saveSnapShot, PERIOD_IN_SECONDS, PERIOD_IN_SECONDS, TimeUnit.SECONDS);
        logger.info("Game context save routine started (every {}s).", PERIOD_IN_SECONDS);
    }

    private void saveSnapShot() {
        MatchSnapshot snapshot = currentSnapshot;
        if (snapshot == null) return;
        backupService.saveSnapshot(snapshot).exceptionally(ex -> {
            logger.warn("Periodic snapshot failed", ex);
            return null;
        });
    }

    public void saveFinalSnapshot(MatchSnapshot finalSnapshot) {
        if (periodicTask != null) {
            periodicTask.cancel(false);
        }
        try {
            CompletableFuture.allOf(getAllOf(finalSnapshot)).get(5, TimeUnit.SECONDS);
            closeConnections();
            logger.info("Final game context and results saved.");
        } catch (Exception e) {
            logger.error("Final backup and/or results save failed or timed out", e);
        }
    }

    private void closeConnections() {
        this.backupService.closeConnection();
        this.resultsService.closeConnection();
    }

    private CompletableFuture<?>[] getAllOf(MatchSnapshot finalSnapshot) {
        CompletableFuture<?> backupSaved = backupService.saveSnapshot(finalSnapshot);
        CompletableFuture<?>[] resultsSaved = resultsService.saveResults(finalSnapshot);
        CompletableFuture<?>[] allOf = new CompletableFuture[resultsSaved.length + 1];
        int index = 0;
        allOf[index++] = backupSaved;
        for (CompletableFuture<?> completableFuture : resultsSaved) {
            allOf[index++] = completableFuture;
        }
        return allOf;
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
