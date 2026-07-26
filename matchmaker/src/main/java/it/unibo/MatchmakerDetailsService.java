package it.unibo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.boot.webflux.*;

import it.unibo.dto.JoinLobbyResponse;
import it.unibo.dto.LobbyTypeResponse;
import it.unibo.mongodb.LobbyInfoMongoDB;
import it.unibo.mongodb.MatchInfoMongoDB;
import it.unibo.mongodb.ShortTermLobbyRepository;
import it.unibo.mongodb.ShortTermMatchRepository;
import reactor.core.publisher.Mono;

//TODO: Check: https://dev.to/adamthedeveloper/spring-webflux-when-to-use-it-and-how-to-build-with-it-5a6e
/**
 * 
 * Manages all the actions required by the Matchmaker.
 */
@Service
public class MatchmakerDetailsService {

    private static final int LOBBY_SIZE = 4;
    private static final String MANAGER = "";
    
    @Autowired
    private ShortTermLobbyRepository lobbyCollection;

    @Autowired
    private ShortTermMatchRepository matchCollection;

    /**
     * Find a lobby if it presents otherwise it throws an Exception.
     * @param lobbyId the identifier of the lobby.
     * @return the LobbyInfoMongoDB when it exists or throws an Exception because the lobby is not in the database. 
     * @throws Exception
     */
    public LobbyInfoMongoDB getLobby(String lobbyId) throws Exception {
        return lobbyCollection.findById(lobbyId)
            .orElseThrow(() -> new Exception("Lobby does not exist!"));
    }

    /**
     * Deals with deleting a user from a specific lobby.
     * @param lobbyId the identifier of the lobby.
     * @param username the identifier of the player.
     */
    public void deleteUserByLobbyId(String lobbyId, String username) {
        lobbyCollection.removeUserFromLobby(lobbyId, username);
    }

    /**
     * Checks an existing lobby for the player and otherwise it creates a new ones.
     * @param username the player identifier.
     * @param map the name of the map chosen by the player.
     * @return a Response containing the type of the response and the lobby id.
     */
    @Async  //TODO: ok? nel caso fare una classe servizio a parte per tutti metodi che si eseguono in modo asyn.
    public JoinLobbyResponse checkForLobby(String username, String map) {
        Optional<LobbyInfoMongoDB> lobby = lobbyCollection.findByUsername(username);
        if (lobby.isPresent()) {
            int size = lobby.get().getPlayers().size();
            return size < LOBBY_SIZE ? 
                new JoinLobbyResponse(LobbyTypeResponse.WAITING, lobby.get().getId()) : 
                foundResponse(lobby.get());
        }
        List<LobbyInfoMongoDB> lobbies = lobbyCollection.findByMap(map);
        if (lobbies.isEmpty()) {
            List<String> players = new ArrayList<>();
            players.add(username);
            LobbyInfoMongoDB newLobby = new LobbyInfoMongoDB(map, players);
            lobbyCollection.save(newLobby);
            return new JoinLobbyResponse(LobbyTypeResponse.WAITING, newLobby.getId());
        } else {
            //lobbies.sort(Comparator.comparingInt(l -> l.getPlayers().size()));
            LobbyInfoMongoDB newLobby = lobbies
                                    .stream()
                                    .filter(l -> l.getPlayers().size() < LOBBY_SIZE)
                                    .findFirst()
                                    .get();
            
            newLobby.getPlayers().add(username);
            return newLobby.getPlayers().size() == LOBBY_SIZE ? 
                foundResponse(newLobby) : 
                new JoinLobbyResponse(LobbyTypeResponse.WAITING, newLobby.getId());
        }
    }

    // TODO: quando si ha la risposta con found, aggiungere sulla collection matches un nuovo match
    // e inviare la richiesta al manager.
    private JoinLobbyResponse foundResponse(LobbyInfoMongoDB lobby) {
        // TODO: chiamare il manager.
        /* SYNC
        ResponseEntity<String> result = RestClient
            .create(MANAGER)
            .post()
            .uri(new URI("/"))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(null)
            .retrieve()
            .toEntity(String.class);
        */

        // TODO: check https://docs.spring.io/spring-framework/reference/web/webflux-webclient/client-retrieve.html
        /*
        CompletableFuture<?> result = WebClient
            .create(MANAGER)
            .post()
            .uri(new URI("/"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(null)
            .retrieve()
            .bodyToMono(Object.class).toFuture();
        */
        
        MatchInfoMongoDB match = this.matchCollection.save(
            new MatchInfoMongoDB(lobby.getPlayers(), null));
        
        return new JoinLobbyResponse(LobbyTypeResponse.FOUND, match.getId());
    }

    /**
     * Returns the information for the specified match.
     * @param matchId the identifier of the match.
     * @return the MatchInfoMongoDB when it exists or throws an Exception because the match is not in the database.  
     * @throws Exception
     */
    public MatchInfoMongoDB getMatch(String matchId) throws Exception {
        return matchCollection.findByMatchId(matchId)
            .orElseThrow(() -> new Exception("Match does not exist!"));
    }

}