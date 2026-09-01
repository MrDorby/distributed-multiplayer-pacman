package it.unibo.matchmaker;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import it.unibo.dto.DeleteMatchDTO;
import it.unibo.dto.GameServerRequest;
import it.unibo.dto.JoinLobbyRequest;
import it.unibo.dto.QuitLobbyRequest;
import it.unibo.dto.RemoveFromMatchRequest;

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
    CompletableFuture<ResponseEntity<String>> joinLobby(@RequestBody JoinLobbyRequest join);

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
    CompletableFuture<ResponseEntity<String>> getGameServer(@RequestBody GameServerRequest info);

    /**
     * Request to delete a match on database once it is concluded.
     * @param deleteMatchDTO the identifier of the match to delete on db.
     */
    ResponseEntity<String> deleteMatchOnDB(@RequestBody DeleteMatchDTO deleteMatchDTO);
    
    /**
     * Removes a player from a match if it is not anymore interested in that match.
     * @param remove the request of the user containing the token and the match id.
     * @return a Response empty containing a HttpCode.
     */
    ResponseEntity<Void> removePlayerFromMatch(@RequestBody RemoveFromMatchRequest remove);
}