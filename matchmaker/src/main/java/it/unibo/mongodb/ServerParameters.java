package it.unibo.mongodb;

/**
 * 
 * ServerParameters is a structure containing 
 * the information about the GameServer.
 * @param ip the ip address of the GameServer.
 * @param tcpPort the TCP port used by the GameServer.
 * @param udpPort the UDP port used by the GameServer.
 */
public record ServerParameters(
    String ip,
    int tcpPort,
    int udpPort
) {

}
