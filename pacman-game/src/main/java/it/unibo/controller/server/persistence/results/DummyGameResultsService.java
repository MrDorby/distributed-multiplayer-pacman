package it.unibo.controller.server.persistence.results;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class DummyGameResultsService implements GameResultsService {
    private static final Logger logger = LoggerFactory.getLogger(DummyGameResultsService.class);

    @Override
    public CompletableFuture<Void> saveResults(MatchSnapshot snapshot) {
        logger.debug("Saving results of snapshot taken at {} with matchId: {}", snapshot.timestamp(), snapshot.matchId());
        return CompletableFuture.completedFuture(null);
    }
}
