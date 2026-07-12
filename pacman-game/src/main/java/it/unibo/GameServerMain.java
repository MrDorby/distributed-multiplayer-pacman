package it.unibo;

import it.unibo.controller.server.GameServerFactory;
import it.unibo.controller.server.GameServer;

public class GameServerMain {
    private static final String DEFAULT_MAP_NAME = "map1";
    private static final int DEFAULT_TCP_PORT = 7777 ;
    private static final int DEFAULT_UDP_PORT = 7777;

    /**
     * Launches the game gateway.
     *
     * <p>Usage: {@code GameServerMain [map_name] [tcp_port] [udp_port]}
     */
    static void main(String[] args) throws Exception {
        String mapName = args.length > 0 ? args[0] : DEFAULT_MAP_NAME;
        int tcpPort = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_TCP_PORT;
        int udpPort = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_UDP_PORT;
        GameServer controller = GameServerFactory.createWithoutPersistence(mapName, tcpPort, udpPort);
        controller.start();
    }
}
