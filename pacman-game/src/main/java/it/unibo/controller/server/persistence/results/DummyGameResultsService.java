package it.unibo.controller.server.persistence.results;

import it.unibo.controller.shared.network.dto.GameContextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class DummyGameResultsService implements GameResultsService {
    private static final Logger logger = LoggerFactory.getLogger(DummyGameResultsService.class);

    @Override
    public CompletableFuture<Void> saveResults(GameContextDTO dto) {
        logger.debug("Saving results");
        return CompletableFuture.completedFuture(null);
    }
}
