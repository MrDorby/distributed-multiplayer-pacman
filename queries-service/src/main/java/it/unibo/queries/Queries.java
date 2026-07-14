package it.unibo.queries;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import it.unibo.dto.PublicKeyRequestDTO;

/**
 * Service capable of managing the request for user info stored on a mongoDB.
 */
public interface Queries {
    
    /**
     * The method checks the authenticity of the token sent by the user, letting user accessing informations.
     * @param token a String representing the token provided.
     * @return a Response containing a OK message. 
     */
    ResponseEntity<String> checkTokenPermission(@RequestBody String token);

    /**
     * Extracts the information stored of the specified player.
     * @param username the player identifier.
     * @return a Response containing all the data requested.
     */
    ResponseEntity<String> getPlayerInfo(@RequestBody String username);

    /**
     * Lets the two parties to exchange their public key to perform confidentiality.
     * @param publicKeyClientDTO the initial request.
     * @return a Response containing the public key of the Queries Service.
     */
    ResponseEntity<String> syn(@RequestBody PublicKeyRequestDTO publicKeyClientDTO);

}
