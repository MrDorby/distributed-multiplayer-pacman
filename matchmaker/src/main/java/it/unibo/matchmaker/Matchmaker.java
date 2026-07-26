package it.unibo.matchmaker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import it.unibo.dto.GameServerRequest;
import it.unibo.dto.JoinLobbyRequest;
import it.unibo.dto.QuitLobbyRequest;

/**
 * Concept of the service that manages the matchmaking part.
 */
public interface Matchmaker {

    /**
     * Call to let the user to be inserted in a lobby queue.
     * @param join the message transmitted with token and map.
     * @return a Response containing the type and the specific identifier.
     * Type WAITING will give a lobbyId, while Type FOUND will give matchId.
     */
    ResponseEntity<String> joinLobby(@RequestBody JoinLobbyRequest join);

    /**
     * Call to let the user to be deleted from the lobby queue.
     * @param quit the message transmitted with token and lobbyId.
     * @return a Response containing a confirmation message.
     */
    ResponseEntity<String> quitLobby(@RequestBody QuitLobbyRequest quit);

    /**
     * Call to let the user gets the information about the GameServer.
     * @param info the initial request.
     * @return a Response containing the IP and ports of the GameServer.
     */
    ResponseEntity<String> getGameServer(@RequestBody GameServerRequest info);
    
}