package it.unibo;

import it.unibo.controller.server.GameServerFactory;
import it.unibo.controller.server.GameServer;

import java.util.UUID;

public class GameServerMain {
    private static final String DEFAULT_MAP_NAME = "map1";
    private static final int DEFAULT_TCP_PORT = 7777;
    private static final int DEFAULT_UDP_PORT = 7777;

    /**
     * Launches the game server.
     *
     * <p>Usage: {@code GameServerMain} (launches with local defaults and a random matchId)
     * <p>or: {@code GameServerMain [match_id] [map_name] [tcp_port] [udp_port]} (all 4 are mandatory)
     */
    static void main(String[] args) throws Exception {
        String matchId;
        String mapName;
        int tcpPort;
        int udpPort;

        if (args.length == 0) {
            matchId = UUID.randomUUID().toString();
            mapName = DEFAULT_MAP_NAME;
            tcpPort = DEFAULT_TCP_PORT;
            udpPort = DEFAULT_UDP_PORT;
        } else if (args.length == 4) {
            matchId = args[0];
            mapName = args[1];
            tcpPort = Integer.parseInt(args[2]);
            udpPort = Integer.parseInt(args[3]);
        } else {
            throw new IllegalArgumentException("Invalid usage. Provide either NO arguments for defaults, or EXACTLY 4 arguments: " + "<match_id> <map_name> <tcp_port> <udp_port>");
        }
        GameServer server = GameServerFactory.createWithFileSystemPersistence(matchId, mapName, tcpPort, udpPort);
        server.start();
    }
}