package it.unibo.controller.server.network.sockets;

import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.handlers.UdpHandler;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;

/**
 * Defines the contract for a game server gateway utilizing both
 * TCP and UDP protocols to handle real-time game communications.
 */
public interface GameServerGateway {
    /**
     * Registers a specific handler to process incoming TCP packets of a given type.
     */
    void addTcpHandler(PacketType type, TcpHandler handler);

    /**
     * Registers a specific handler to process incoming UDP packets of a given type.
     */
    void addUdpHandler(PacketType type, UdpHandler handler);

    /**
     * Starts the game network gateway.
     */
    void start() throws InterruptedException;

    /**
     * Stops the game network gateway.
     */
    void stop();

    /**
     * Sends a packet to a specific connected player via TCP.
     */
    void sendTcp(String username, NetworkPacket packet);

    /**
     * Sends a packet to a specific player via UDP.
     */
    void sendUdp(String username, NetworkPacket packet);

    /**
     * Broadcasts a packet to all currently connected players via TCP.
     */
    void broadcastTcp(NetworkPacket packet);

    /**
     * Broadcasts a packet to all currently connected players via UDP.
     */
    void broadcastUdp(NetworkPacket packet);
}
