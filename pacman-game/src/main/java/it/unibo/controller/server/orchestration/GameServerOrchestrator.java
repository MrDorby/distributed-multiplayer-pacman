package it.unibo.controller.server.orchestration;

/**
 * Orchestrator responsible for implementing the GameServer's heartbeat mechanism and notifying the current
 * status of the GameServer to the system.
 */
public interface GameServerOrchestrator {
    /**
     * Starts the GameServer's heartbeat mechanism and signals that the GameServer is ready to accept incoming player
     * connections.
     */
    void start();
    // void allocate(); TODO: implement separately or within ready() (documenting it properly)?
    //void startHeartbeat(); TODO: remove?
    //void stopHeartbeat(); TODO: remove?

    /**
     * Stops the GameServer's heartbeat mechanism and signals that the GameServer is shutting down and can be
     * deallocated.
     */
    void shutdown();
}
