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
     * @return a Response.
     */
    ResponseEntity<String> joinLobby(@RequestBody JoinLobbyRequest join);

    /**
     * Call to let the user to be deleted from the lobby queue.
     * @param quit the message transmitted with token and lobbyId.
     * @return a Response.
     */
    ResponseEntity<String> quitLobby(@RequestBody QuitLobbyRequest quit);

    //TODO: check it.
    /**
     * 
     * @param info
     * @return
     */
    ResponseEntity<String> getGameServer(@RequestBody GameServerRequest info);

    //TODO: ADD METHODS.
    
}