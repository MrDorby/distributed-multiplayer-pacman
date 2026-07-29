package it.unibo.gameservermanager;

import it.unibo.dto.GameServerCheckResults;
import it.unibo.dto.GameServerInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST interface for the GameServerManager.
 */
public interface GameServerManagerController {
    /**
     * Creates a new GameServer for the specified match, and returns information about it once it's been allocated.
     * @param matchID the unique ID of the match.
     * @return a record containing information about the GameServer. Information provided are:
     * <ul>
     *     <li>The GameServer's unique name.</li>
     *     <li>Its IP address.</li>
     *     <li>Its TCP and UDP port numbers.</li>
     * </ul>
     */
    ResponseEntity<GameServerInfo> createGameServer(@RequestBody String matchID);

    /**
     * Checks the current status of the GameServer associated to the specified match.
     * If the GameServer's status is {@code HEALTHY} or {@code NOT_FOUND}, does nothing.
     * If the status is {@code UNHEALTHY}, decides whether to instantiate a recovery GameServer to take its place,
     * based on the match's time left. If a recovery GameServer has been instantiated, also returns the information
     * about it.
     * @param matchID the unique ID of the match.
     * @return the results of the check on the GameServer, which contain its current status and can contain the
     * information about the newly instantiated recovery GameServer.
     */
    ResponseEntity<GameServerCheckResults> checkGameServer(@RequestBody String matchID);

    /**
     * Signals that the specified match is over, so that related data can be deallocated.
     * After receiving this signal, the GameServerManager will send a similar signal to the Matchmaker.
     * @param matchID the unique ID of the match.
     * @return the result of the signaling operation.
     */
    ResponseEntity<String> matchEnded(@RequestBody String matchID);
}
