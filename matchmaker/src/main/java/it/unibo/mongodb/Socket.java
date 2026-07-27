package it.unibo.mongodb;

/**
 * 
 * Socket is a structure containing 
 * the information about the GameServer.
 * @param ip the ip address of the GameServer.
 * @param port the port used by the GameServer.
 */
public record Socket(
    String ip,
    int port
) {

}
