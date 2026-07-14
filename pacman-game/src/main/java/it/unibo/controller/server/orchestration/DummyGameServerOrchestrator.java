package it.unibo.controller.server.orchestration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DummyGameServerOrchestrator implements GameServerOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(DummyGameServerOrchestrator.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final long PERIOD_IN_MILLIS = 5000;


    @Override
    public void ready() {
        logger.debug("Notifying orchestrator that the server is ready");
    }

    @Override
    public void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            logger.debug("Sending heartbeat to orchestrator");
        }, 0, PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopHeartbeat() {
        logger.debug("Stopping heartbeat scheduler");
        scheduler.shutdown();
    }

    @Override
    public void shutdown() {
        logger.debug("Notifying orchestrator that the server has completed its game and is shutting down");
        scheduler.shutdown();
    }
}
