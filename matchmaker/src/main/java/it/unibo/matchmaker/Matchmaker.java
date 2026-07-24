package it.unibo.matchmaker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import it.unibo.dto.JoinLobbyRequest;
import it.unibo.dto.QuitLobbyRequest;

/**
 * Matchmaker
 */
public interface Matchmaker {


    /**
     * 
     * @param join
     * @return
     */
    ResponseEntity<String> joinLobby(@RequestBody JoinLobbyRequest join);

    /**
     * 
     * @param quit
     * @return
     */
    ResponseEntity<String> quitLobby(@RequestBody QuitLobbyRequest quit);

    //TODO: ADD METHODS.
    
}