package it.unibo.controller.shared.network.sockets.channel;

public class NetworkConfig {
    private NetworkConfig() {}

    // Heartbeat Constants
    // Formula: Read Timeout >= (Ping Interval * 2) + Buffer Zone.

    /**
     * How often the client sends a heartbeat ping if it hasn't written any data.
     */
    public static final int CLIENT_WRITE_PING_INTERVAL_SECONDS = 3;

    /**
     * How long the client waits for a payload from the server before declaring it dead.
     */
    public static final int CLIENT_READ_TIMEOUT_SECONDS = 7;

    /**
     * How long the server waits for a player to transmit any payload before assuming their connection has stalled.
     */
    public static final int SERVER_READ_TIMEOUT_SECONDS = 8;
}
