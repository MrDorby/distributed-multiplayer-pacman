package it.unibo.controller.server.orchestration;

public interface GameServerOrchestrator {
    void ready();
    void startHeartbeat();
    void stopHeartbeat();
    void shutdown();
}
