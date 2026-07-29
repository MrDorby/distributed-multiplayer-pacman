package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transmitted between the Matchmaker and the GameServer Manager.
 * Also used as response for the GameServer request. 
 * @param name the GameServer's unique name.
 * @param ip the GameServer's IP address.
 * @param tcpPort the GameServer's TCP port.
 * @param udpPort the GameServer's UDP port.
 */
public record GameServerInfo(
    @JsonProperty("name") String name,
    @JsonProperty("ip") String ip,
    @JsonProperty("tcp-port") int tcpPort,
    @JsonProperty("udp-port") int udpPort) {
    
}