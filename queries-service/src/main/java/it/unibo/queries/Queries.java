package it.unibo.queries;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 
 * Queries
 */
public interface Queries {
    
    /**
     * 
     * @param token
     * @return
     */
    ResponseEntity<String> checkTokenPermission(@RequestBody String token);

    /**
     * 
     * @param username
     * @return
     */
    ResponseEntity<String> getPlayerInfo(@RequestBody String username);

}
