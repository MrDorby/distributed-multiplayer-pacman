package it.unibo.controller.server.persistence;

import it.unibo.controller.server.persistence.snapshot.GameSnapshotRepository;
import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.results.GameResultsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Coordinates periodic and end-of-game persistence of game state.
 * <p>
 * While a game is running, periodically backs up the most recently supplied game
 * context via {@link GameSnapshotRepository}. Once the game ends, cancels the periodic
 * schedule and concurrently saves a final backup and the game's results via
 * {@link GameResultsRepository}.
 */
public class GamePersistenceManager implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(GamePersistenceManager.class);
    private static final long PERIOD_IN_SECONDS = 10;

    private final GameSnapshotRepository snapshotRepository;
    private final GameResultsRepository resultsRepository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile MatchSnapshot currentSnapshot;
    private ScheduledFuture<?> periodicTask;

    public GamePersistenceManager(GameSnapshotRepository snapshotRepository, GameResultsRepository resultsRepository) {
        this.snapshotRepository = snapshotRepository;
        this.resultsRepository = resultsRepository;
    }

    public void updateSnapshot(MatchSnapshot snapshot) {
        this.currentSnapshot = snapshot;
    }

    public void start() {
        periodicTask = scheduler.scheduleAtFixedRate(this::saveSnapShot, PERIOD_IN_SECONDS, PERIOD_IN_SECONDS, TimeUnit.SECONDS);
        logger.info("Game context save routine started (every {}s).", PERIOD_IN_SECONDS);
    }

    private void saveSnapShot() {
        MatchSnapshot snapshot = currentSnapshot;
        if (snapshot == null) return;
        snapshotRepository.saveSnapshot(snapshot).exceptionally(ex -> {
            logger.warn("Periodic snapshot failed", ex);
            return null;
        });
    }

    public void saveFinalSnapshot(MatchSnapshot finalSnapshot) {
        if (periodicTask != null) {
            periodicTask.cancel(false);
        }
        try {
            CompletableFuture.allOf(
                    snapshotRepository.saveSnapshot(finalSnapshot),
                    resultsRepository.saveResults(finalSnapshot))
                    .get(5, TimeUnit.SECONDS);
            logger.info("Final game context and results saved.");
        } catch (Exception e) {
            logger.error("Final backup and/or results save failed or timed out", e);
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        stopScheduler();
        try {
            snapshotRepository.close();
        } catch (Exception e) {
            logger.warn("Failed to close snapshot repository cleanly", e);
        }
        try {
            resultsRepository.close();
        } catch (Exception e) {
            logger.warn("Failed to close results service cleanly", e);
        }
    }

    public void stopScheduler() {
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
