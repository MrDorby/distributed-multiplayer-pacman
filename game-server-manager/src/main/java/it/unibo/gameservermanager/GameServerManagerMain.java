package it.unibo.gameservermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entrypoint for the GameServerManager service.<br>
 * The controller used by the GameServerManager requires the following parameters, in the form of environment
 * variables:
 * <ul>
 *     <li>{@code MATCHMAKER_URI} Mandatory. Specifies the absolute base URI of the Matchmaker service (port number
 *     included).</li>
 *     <li>{@code MIN_TIME_LEFT} Optional. Specifies the minimum match's time left for the GameServerManager to
 *     instantiate a recovery GameServer, instead of just letting the match end. A recovery GameServer will thus
 *     be instantiated only if the match's time left is higher or equal than the value of this parameter.
 *     Defaults to 5000 milliseconds.</li>
 *     <li>{@code BACKUP_SERVICE_URL} and {@code RESULTS_SERVICE_URL} Optional. Database connection URIs required by
 *     the GameServer. Remote database persistence for GameServers is enabled only if both of these variables are
 *     specified. If one or both variables are not specified, then the created GameServers will use local persistence.</li>
 * </ul>
 */
@SpringBootApplication
public class GameServerManagerMain {
    static void main(String[] args) {
        SpringApplication.run(GameServerManagerMain.class, args);
    }
}
