package it.unibo;

import it.unibo.controller.server.GameServerBuilder;
import it.unibo.controller.server.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Entry point for launching {@link GameServer} instances.
 * <p>
 * This class parses command-line arguments to determine whether to run in Standard Mode
 * (fresh match) or Recovery Mode (restoring from a crashed snapshot), as well as choosing
 * between local (file system) and remote persistence (database).
 *
 * <h2>Defaults</h2>
 * <ul>
 *   <li><b>Execution Mode:</b> Standard Mode (unless {@code --recover} is provided).</li>
 *   <li><b>Persistence Mode:</b>
 *     <ul>
 *       <li>Defaults to {@code --remote} if CLI positional arguments are provided.</li>
 *       <li>Defaults to {@code --local} if zero CLI arguments are provided.</li>
 *     </ul>
 *   </li>
 *   <li><b>Port Defaults:</b> TCP {@code 7777}, UDP {@code 7777}.</li>
 *   <li><b>Map Default:</b> {@code "map1"} (Standard Mode only).</li>
 * </ul>
 *
 * <h2>Supported Flags</h2>
 * <ul>
 *   <li>{@code --recover} : Enables recovery mode. In this mode, {@code mapName} is omitted from positional arguments.</li>
 *   <li>{@code --local}   : Forces local file-system persistence under {@code .temp/matches/}.</li>
 *   <li>{@code --remote}  : Forces remote database persistence. When active, required connection URIs
 *       ({@code BACKUP_SERVICE_URL}, {@code RESULTS_SERVICE_URL}) are loaded dynamically from environment variables.</li>
 * </ul>
 *
 * <h2>Positional Argument Order</h2>
 * Flags can appear anywhere. Non-flag arguments are assigned sequentially based on execution mode:
 *
 * <h3>1. Standard Mode (Default)</h3>
 * <pre>{@code java GameServerMain [flags] <matchId> <mapName> <tcpPort> <udpPort>}</pre>
 * <ul>
 *   <li>{@code matchId} : Unique match identifier string.</li>
 *   <li>{@code mapName} : Name of the map configuration to load.</li>
 *   <li>{@code tcpPort} : TCP port for client communications.</li>
 *   <li>{@code udpPort} : UDP port for client communications.</li>
 * </ul>
 *
 * <h3>2. Recovery Mode ({@code --recover})</h3>
 * <pre>{@code java GameServerMain --recover [flags] <matchId> <tcpPort> <udpPort>}</pre>
 * <i>Note: {@code mapName} is omitted because the game state is reconstructed directly from the snapshot.</i>
 *
 * <h2>Usage Examples</h2>
 * <ul>
 *   <li>{@code java GameServerMain} <br>
 *       Standard local match with auto-generated UUID, default map ("map1"), and ports 7777/7777.</li>
 *   <li>{@code java GameServerMain match-101 map2 7777 8888} <br>
 *       Standard remote match using environment variable URIs for "match-101" on map "map2" listening on 7777/8888.</li>
 *   <li>{@code java GameServerMain --recover match-101 7777 8888} <br>
 *       Recovers "match-101" from remote snapshot using environment variable URIs on ports 7777/8888.</li>
 *   <li>{@code java GameServerMain --local --recover match-101 7777 8888} <br>
 *       Recovers "match-101" from a local file-system snapshot on ports 7777/8888.</li>
 * </ul>
 */
public class GameServerMain {
    private static final Logger logger = LoggerFactory.getLogger(GameServerMain.class);
    private static final String DEFAULT_MAP_NAME = "map1";
    private static final int DEFAULT_TCP_PORT = 7777;
    private static final int DEFAULT_UDP_PORT = 7777;

    static void main(String[] args) throws Exception {
        boolean hasLocalFlag = false;
        boolean hasRemoteFlag = false;
        boolean isRecovery = false;
        String matchId = null;
        String mapName = null;
        Integer tcpPort = null;
        Integer udpPort = null;
        GameServer server;
        try {
            for (String arg : args) {
                if ("--recover".equalsIgnoreCase(arg)) {
                    isRecovery = true;
                } else if ("--local".equalsIgnoreCase(arg)) {
                    hasLocalFlag = true;
                } else if ("--remote".equalsIgnoreCase(arg)) {
                    hasRemoteFlag = true;
                } else if (matchId == null) {
                    matchId = arg;
                } else if (!isRecovery && mapName == null) {
                    mapName = arg;
                } else if (tcpPort == null) {
                    tcpPort = Integer.parseInt(arg);
                } else if (udpPort == null) {
                    udpPort = Integer.parseInt(arg);
                } else {
                    throw new IllegalArgumentException("Excessive argument: '" + arg + "'");
                }
            }
            boolean usesLocalPersistence = hasLocalFlag || (matchId == null && !hasRemoteFlag);
            if (matchId == null) {
                matchId = UUID.randomUUID().toString();
                logger.info("No match ID provided. Defaulting to local persistence with generated matchId: {}", matchId);
            }
            if (tcpPort == null) tcpPort = DEFAULT_TCP_PORT;
            if (udpPort == null) udpPort = DEFAULT_UDP_PORT;
            if (!isRecovery && mapName == null) mapName = DEFAULT_MAP_NAME;

            validateArgs(isRecovery, matchId, hasLocalFlag, hasRemoteFlag, usesLocalPersistence, tcpPort, udpPort);
            GameServerBuilder builder = new GameServerBuilder()
                    .forMatch(matchId)
                    .withPorts(tcpPort, udpPort);
            builder = isRecovery ? builder.asRecovery() : builder.withMap(mapName);
            builder = usesLocalPersistence ? builder.withLocalPersistence() : builder.withRemotePersistence();
            server = builder.build();
        } catch (Exception e) {
            printUsage();
            throw e;
        }
        server.start();
    }

    private static void validateArgs(
            boolean isRecovery,
            String rawMatchId,
            boolean hasLocalFlag,
            boolean hasRemoteFlag,
            boolean usesLocalPersistence,
            int tcpPort,
            int udpPort
    ) {
        // Flag compatibility: Cannot specify both --local and --remote
        if (hasLocalFlag && hasRemoteFlag) {
            throw new IllegalArgumentException("Cannot specify both --local and --remote persistence flags");
        }
        // Recovery mode requires an explicit target match ID
        if (isRecovery && rawMatchId == null) {
            throw new IllegalArgumentException("Recovery mode (--recover) requires an explicit target matchId parameter");
        }
        // Port Range Validation (1 - 65535)
        if (tcpPort < 1 || tcpPort > 65535) {
            throw new IllegalArgumentException("TCP port must be between 1 and 65535, got: " + tcpPort);
        }
        if (udpPort < 1 || udpPort > 65535) {
            throw new IllegalArgumentException("UDP port must be between 1 and 65535, got: " + udpPort);
        }
        // Environment Variable Validation for Remote Persistence
        if (!usesLocalPersistence) {
            String backupUrl = System.getenv("BACKUP_SERVICE_URL");
            String resultsUrl = System.getenv("RESULTS_SERVICE_URL");
            if (backupUrl == null || backupUrl.isBlank() || resultsUrl == null || resultsUrl.isBlank()) {
                throw new IllegalStateException("Remote persistence requires BACKUP_SERVICE_URL and RESULTS_SERVICE_URL environment variables to be set.");
            }
        }
    }

    /**
     * Prints CLI usage instructions when argument parsing or validation fails.
     */
    private static void printUsage() {
        System.err.println("""
            ================================================================================================
            Usage:
              Standard Mode:  java GameServerMain [--local|--remote] <matchId> <mapName> [tcpPort] [udpPort]
              Recovery Mode:  java GameServerMain --recover [--local|--remote] <matchId> [tcpPort] [udpPort]
            
            Flags:
              --recover : Enables recovery mode (omits <mapName> parameter). Requires <matchId>.
              --local   : Uses local file-system storage.
              --remote  : Uses database persistence (requires environment variables).
            
            Environment Variables (Required when using --remote):
              BACKUP_SERVICE_URL   : Connection URI for snapshot storage service.
              RESULTS_SERVICE_URL  : Connection URI for results service.
            
            Defaults:
              - Ports: TCP 7777, UDP 7777
              - Map: map1
              - Running with zero args launches local standard mode with auto-generated matchId.
            ================================================================================================
            """);
    }
}