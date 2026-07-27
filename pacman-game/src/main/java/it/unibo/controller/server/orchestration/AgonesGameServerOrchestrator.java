//package it.unibo.controller.server.orchestration; TODO: did not work, got replaced by AgonesRESTGameServerOrchestrator
//
//import net.infumia.agones4j.Agones;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.Duration;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
///**
// * A GameServerOrchestrator that implements the orchestration according to Agones's protocol.
// */
//public class AgonesGameServerOrchestrator implements GameServerOrchestrator {
//    private final ExecutorService gameServerWatcherExecutor;
//    private final ScheduledExecutorService healthCheckExecutor;
//    private final Agones agones;
//    private final Logger logger = LoggerFactory.getLogger(AgonesGameServerOrchestrator.class);
//
//    public AgonesGameServerOrchestrator() {
//        this.gameServerWatcherExecutor = Executors.newSingleThreadExecutor();
//        this.healthCheckExecutor = Executors.newSingleThreadScheduledExecutor();
//        this.agones = Agones.builder()
//                .withGameServerWatcherExecutor(this.gameServerWatcherExecutor)
//                .withHealthCheck(Duration.ofSeconds(2L), Duration.ofSeconds(2L))
//                .withHealthCheckExecutor(this.healthCheckExecutor)
//                .build();
//        if (this.agones.canWatchGameServer()) {
//            this.agones.addGameServerWatcher(gameServer ->
//                this.logger.info("Game server updated: {}", gameServer));
//        }
//        this.agones.healthCheckStream();
//    }
//
//    @Override
//    public void start() {
//        startHeartbeat();
//        this.logger.info("Starting and allocating game server");
//        this.agones.ready();
//        this.agones.allocate();
//    }
//
//    private void startHeartbeat() {
//        if (this.agones.canHealthCheck()) {
//            this.logger.info("Starting Agones health checking");
//            this.agones.startHealthChecking();
//        }
//    }
//
//    private void stopHeartbeat() {
//        this.logger.info("Stopping Agones health checking");
//        this.agones.stopHealthChecking();
//        gameServerWatcherExecutor.shutdown();
//        healthCheckExecutor.shutdown();
//    }
//
//    @Override
//    public void shutdown() {
//        stopHeartbeat();
//        this.logger.info("Shutting down game server orchestration");
//        this.agones.shutdown();
//    }
//}
