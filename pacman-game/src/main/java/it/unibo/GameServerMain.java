package it.unibo;

import it.unibo.controller.server.GameServerBuilder;
import it.unibo.controller.server.GameServer;
import it.unibo.controller.server.orchestration.AgonesRESTGameServerOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Entry point for launching {@link GameServer} instances.
 *
 * <h2>Supported Flags</h2>
 * <ul>
 *   <li>{@code --recover} : Restores state from snapshot. Omits {@code mapName} positional argument.</li>
 *   <li>{@code --local}   : Uses local file-system and in-memory persistence instead of remote DB.</li>
 *   <li>{@code --orchestrated} : Enables cluster orchestration. To be used when deploying the GameServer on a cluster.
 *        When active, the port used to communicate with the sidecar container ({@code AGONES_SIDECAR_HTTP_PORT}) can be
 *        set dynamically via an environment variable. If no variable is specified, this parameter is set by default
 *        to port 9358.</li>
 * </ul>
 *
 * <h2>Positional Arguments</h2>
 * <ul>
 * <li><b>Standard Mode:</b> {@code java -jar server.jar [flags] <matchId> <mapName> [tcpPort] [udpPort]}</li>
 * <li><b>Recovery Mode:</b> {@code java -jar server.jar --recover [flags] <matchId> [tcpPort] [udpPort]}</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * <ul>
 *   <li>{@code java -jar game-server.jar} <br>
 *       Launches a standard match with auto-generated matchId on map1 using ports 7777/7777.</li>
 *   <li>{@code java -jar game-server.jar match-101 map2 7777 8888} <br>
 *       Launches "match-101" on "map2" listening on custom TCP/UDP ports.</li>
 *   <li>{@code java -jar game-server.jar --orchestrated match-101 map2 7777 8888} <br>
 *       Launches "match-101" with Agones cluster orchestration enabled.</li>
 *   <li>{@code java -jar game-server.jar --recover match-101 7777 8888} <br>
 *       Restores "match-101" from its latest remote DB snapshot.</li>
 *   <li>{@code java -jar game-server.jar --local --recover match-101 7777 8888} <br>
 *       Restores "match-101" from a local file-system snapshot.</li>
 * </ul>
 */
public class GameServerMain {
    private static final Logger logger = LoggerFactory.getLogger(GameServerMain.class);
    private static final String DEFAULT_MAP_NAME = "map1";
    private static final int DEFAULT_TCP_PORT = 7777;
    private static final int DEFAULT_UDP_PORT = 7777;

    private static boolean isRecovery = false;
    private static boolean isLocal = false;
    private static boolean isOrchestrated = false;

    private static String rawMatchId = null;
    private static String matchId = null;
    private static String mapName = null;
    private static Integer tcpPort = null;
    private static Integer udpPort = null;

    static void main(String[] args) throws Exception {
        GameServer server;
        try {
            parseArgs(args);
            validateArgs();
            applyDefaults();
            GameServerBuilder builder = new GameServerBuilder().forMatch(matchId).withPorts(tcpPort, udpPort);
            builder = isRecovery ? builder.asRecovery() : builder.asWhitelisted().withMap(mapName);
            builder = isLocal ? builder.withLocalPersistence() : builder.withRemotePersistence();
            if (isOrchestrated) {
                builder = builder.withOrchestrator(new AgonesRESTGameServerOrchestrator());
            }
            server = builder.build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            printUsage();
            throw e;
        }
        server.start();
    }

    private static void parseArgs(String[] args) {
        for (String arg : args) {
            if ("--recover".equalsIgnoreCase(arg)) {
                isRecovery = true;
            } else if ("--local".equalsIgnoreCase(arg)) {
                isLocal = true;
            } else if ("--orchestrated".equalsIgnoreCase(arg)) {
                isOrchestrated = true;
            } else if (rawMatchId == null) {
                rawMatchId = arg;
                matchId = arg;
            } else if (mapName == null) {
                mapName = arg;
            } else if (tcpPort == null) {
                tcpPort = Integer.parseInt(arg);
            } else if (udpPort == null) {
                udpPort = Integer.parseInt(arg);
            } else {
                throw new IllegalArgumentException("Excessive argument: '" + arg + "'");
            }
        }
    }

    private static void applyDefaults() {
        if (matchId == null) {
            matchId = UUID.randomUUID().toString();
            logger.info("No match ID provided. Generating matchId: {}", matchId);
        }
        if (tcpPort == null) tcpPort = DEFAULT_TCP_PORT;
        if (udpPort == null) udpPort = DEFAULT_UDP_PORT;
        if (!isRecovery && mapName == null) mapName = DEFAULT_MAP_NAME;
    }

    private static void validateArgs() {
        if (isRecovery && rawMatchId == null) {
            throw new IllegalArgumentException("Recovery mode requires an explicit target <matchId> parameter.");
        }
        if (tcpPort != null && (tcpPort < 1 || tcpPort > 65535)) {
            throw new IllegalArgumentException("TCP port must be between 1 and 65535, got: " + tcpPort);
        }
        if (udpPort != null && (udpPort < 1 || udpPort > 65535)) {
            throw new IllegalArgumentException("UDP port must be between 1 and 65535, got: " + udpPort);
        }
        if (!isLocal) {
            String shortTermDbUri = System.getenv("SHORT_TERM_DB_URI");
            String longTermDbUri = System.getenv("LONG_TERM_DB_URI");
            if (shortTermDbUri == null || shortTermDbUri.isBlank() || longTermDbUri == null || longTermDbUri.isBlank()) {
                throw new IllegalStateException("Remote persistence requires SHORT_TERM_DB_URI and LONG_TERM_DB_URI environment variables to be set.");
            }
        }
    }

    private static void printUsage() {
        System.err.println("""
            ================================================================================================
            Usage:
              Standard Mode:  java -jar game-server.jar [--local] [--orchestrated] <matchId> <mapName> [tcpPort] [udpPort]
              Recovery Mode:  java -jar game-server.jar --recover [--local] [--orchestrated] <matchId> [tcpPort] [udpPort]
            
            Flags:
              --recover : Launches in recovery mode using latest snapshot. Requires <matchId> and ignores <mapName>.
              --local   : Uses local file system and in-memory storage instead of default remote persistence.
              --orchestrated : Enables cluster orchestration of the GameServer.
            
            Environment Variables:
              - Required for Remote Persistence (default, when --local is NOT set):
                SHORT_TERM_DB_URI       : Connection URI for snapshot storage service.
                LONG_TERM_DB_URI        : Connection URI for results service.
              - Optional when using --orchestrated:
                AGONES_SIDECAR_HTTP_PORT : Port to be used when interacting with the GameServer's sidecar container.
                Defaults to 9358.
            
            Defaults:
              - Map: map1
              - Ports: TCP 7777, UDP 7777
              - Running with zero args launches standard remote mode with an auto-generated matchId.
            ================================================================================================
            """);
    }
}