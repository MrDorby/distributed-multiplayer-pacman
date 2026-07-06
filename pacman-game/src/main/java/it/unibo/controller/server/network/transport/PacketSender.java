package it.unibo.controller.server.network.transport;

public interface PacketSender {
    void sendTcp(String username, byte packetTypeId, Object packet);

    void sendUdp(String username, byte packetTypeId, Object packet);

    void broadcastTcp(byte packetTypeId, Object packet);

    void broadcastUdp(byte packetTypeId, Object packet);
}