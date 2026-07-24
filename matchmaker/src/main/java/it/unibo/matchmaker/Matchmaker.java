package it.unibo.matchmaker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Matchmaker
 */
public interface Matchmaker {

    /**
     * 
     * @param token
     * @return
     */
    ResponseEntity<String> joinLobby(@RequestBody String token);

    /**
     * 
     * @param token
     * @return
     */
    ResponseEntity<String> quitLobby(@RequestBody String token);

    //TODO: ADD METHODS.
    
}