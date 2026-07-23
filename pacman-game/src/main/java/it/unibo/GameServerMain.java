package it.unibo;

import it.unibo.controller.server.GameServerFactory;
import it.unibo.controller.server.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class GameServerMain {
    private static final Logger logger = LoggerFactory.getLogger(GameServerMain.class);

    private static final String DEFAULT_MAP_NAME = "map1";
    private static final int DEFAULT_TCP_PORT = 7777;
    private static final int DEFAULT_UDP_PORT = 7777;

    /**
     * Launches the game server in either standard or recovery mode.
     * <p>
     * Usage Modes:
     * <ul>
     *   <li><b>Default:</b> {@code java GameServerMain} (launches standard game with local defaults and random matchId)</li>
     *   <li><b>Standard Match:</b> {@code java GameServerMain <match_id> <map_name> <tcp_port> <udp_port>}</li>
     *   <li><b>Recovery Match:</b> {@code java GameServerMain --recover <match_id> <tcp_port> <udp_port>}</li>
     * </ul>
     */
    static void main(String[] args) throws Exception {
        GameServer server;
        if (args.length == 0) {
            String matchId = UUID.randomUUID().toString();
            logger.info("Starting default local game server with matchId: {}", matchId);
            server = GameServerFactory.createWithLocalPersistence(
                    matchId,
                    DEFAULT_MAP_NAME,
                    DEFAULT_TCP_PORT,
                    DEFAULT_UDP_PORT
            );

        } else if (args.length == 4 && "--recover".equalsIgnoreCase(args[0])) {
            // Recovery Mode: --recover <matchId> <tcpPort> <udpPort>
            String matchId = args[1];
            int tcpPort = Integer.parseInt(args[2]);
            int udpPort = Integer.parseInt(args[3]);
            logger.info("Attempting recovery for matchId: {} on ports TCP:{}, UDP:{}", matchId, tcpPort, udpPort);
            server = GameServerFactory.createRecoveryServerWithLocalPersistence(
                    matchId,
                    tcpPort,
                    udpPort
            );
        } else if (args.length == 4) {
            // Standard Mode: <matchId> <mapName> <tcpPort> <udpPort>
            String matchId = args[0];
            String mapName = args[1];
            int tcpPort = Integer.parseInt(args[2]);
            int udpPort = Integer.parseInt(args[3]);
            logger.info("Starting standard local game server [matchId: {}, map: {}, ports TCP:{}, UDP:{}]",
                    matchId, mapName, tcpPort, udpPort);
            server = GameServerFactory.createWithLocalPersistence(
                    matchId,
                    mapName,
                    tcpPort,
                    udpPort
            );
        } else {
            throw new IllegalArgumentException("""
                    Invalid CLI arguments. Expected one of the following formats:
                    1) Default local run: (no arguments)
                    2) Standard run:      <match_id> <map_name> <tcp_port> <udp_port>
                    3) Recovery run:      --recover <match_id> <tcp_port> <udp_port>
                    """);
        }
        server.start();
    }
}