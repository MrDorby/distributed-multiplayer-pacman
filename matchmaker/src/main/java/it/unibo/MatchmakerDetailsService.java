package it.unibo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;


import it.unibo.dto.GameServerResponse;
import it.unibo.dto.JoinLobbyResponse;
import it.unibo.dto.LobbyTypeResponse;
import it.unibo.mongodb.LobbyInfoMongoDB;
import it.unibo.mongodb.MatchInfoMongoDB;
import it.unibo.mongodb.MongoBackground;
import it.unibo.mongodb.ShortTermLobbyRepository;
import it.unibo.mongodb.ShortTermMatchRepository;

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

    @Autowired
    private MongoBackground mongoBackground;

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
    public JoinLobbyResponse checkForLobby(String username, String map) throws Exception {
        //TODO AGGIUNGERE ANCHE IL LOBBY ID O FARE NUOVO ENDPOINT PER IL WAITING?
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
            LobbyInfoMongoDB newLobby = new LobbyInfoMongoDB("", map, players, 0);
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
            LobbyInfoMongoDB res = this.lobbyCollection.save(newLobby);
            return res.getPlayers().size() == LOBBY_SIZE ? 
                foundResponse(res) : 
                new JoinLobbyResponse(LobbyTypeResponse.WAITING, res.getId());
        }
    }

    /* Deals with the management of the FOUND type response. */
    private JoinLobbyResponse foundResponse(LobbyInfoMongoDB lobby) throws Exception {
        GameServerResponse gameServer = null;
        if (lobby.getMatchId().isEmpty() | lobby.getMatchId() == null) {
            /* SYNC */
            ResponseEntity<String> result = RestClient
                .create(MANAGER)
                .post()
                .uri(new URI("/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(null)  // TODO: it will contain the lobby id.
                .retrieve()
                .toEntity(String.class);


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

            ObjectMapper mapper = new ObjectMapper();
            gameServer = mapper.readValue(result.getBody(), GameServerResponse.class);

            this.mongoBackground.saveMatchInfo(
                lobby,
                gameServer,
                lobbyCollection,
                matchCollection
            );
        }
        JoinLobbyResponse response = new JoinLobbyResponse(LobbyTypeResponse.FOUND, lobby.getMatchId());
        this.mongoBackground.deleteLobby(lobby, lobbyCollection);
        return response;
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