package it.unibo.controller.server.persistence.results;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class DummyGameResultsService implements GameResultsService {
    private static final Logger logger = LoggerFactory.getLogger(DummyGameResultsService.class);

    @Override
    public CompletableFuture<?>[] saveResults(MatchSnapshot snapshot) {
        logger.debug("Saving results of snapshot taken at {} with matchId: {}", snapshot.timestamp(), snapshot.matchId());
        CompletableFuture<?>[] ft = new CompletableFuture[1];
        ft[0] = CompletableFuture.completedFuture(null); 
        return ft;
    }

    @Override
    public void closeConnection() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'closeConnection'");
    }
}
