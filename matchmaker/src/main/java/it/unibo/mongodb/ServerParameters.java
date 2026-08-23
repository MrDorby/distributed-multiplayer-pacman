package it.unibo.mongodb;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * ServerParameters is a structure containing 
 * the information about the GameServer.
 * @param host the ip address of the GameServer.
 * @param tcpPort the TCP port used by the GameServer.
 * @param udpPort the UDP port used by the GameServer.
 */
public record ServerParameters(
    @JsonProperty("host") String host,
    @JsonProperty("tcpPort") int tcpPort,
    @JsonProperty("udpPort") int udpPort
) {

}
