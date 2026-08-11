package it.unibo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.dto.GameServerCheckRequest;
import it.unibo.dto.GameServerCheckResponse;
import it.unibo.dto.GameServerInfo;
import it.unibo.dto.GameServerStatus;
import it.unibo.dto.JoinLobbyResponse;
import it.unibo.dto.LobbyTypeResponse;
import it.unibo.dto.ManagerCreateServer;
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
    private static final String MANAGER = "MANAGER";
    private static final String MANAGER_URI = System.getenv(MANAGER);
    private static final String GAMESERVER_DIR = MANAGER_URI + "/gameserver";
    private static final String CREATE_GAMESERVER_URI =  "/create";
    private static final String CHECK_GAMESERVER_URI = "/check";

    private final Logger logger = LoggerFactory.getLogger(MatchmakerDetailsService.class);
    private final ObjectMapper mapper = new ObjectMapper();
    
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
        Optional<LobbyInfoMongoDB> lobby = lobbyCollection.findByUsername(username);
        if (lobby.isPresent()) {
            this.logger.debug("The lobby exists!");
            int size = lobby.get().getPlayers().size();
            return size < LOBBY_SIZE ? 
                new JoinLobbyResponse(LobbyTypeResponse.WAITING, lobby.get().getId()) : 
                foundResponse(lobby.get());
        }
        List<LobbyInfoMongoDB> lobbies = lobbyCollection.findByMap(map);
        if (lobbies.isEmpty()) {
            this.logger.debug("The lobby list is empty!");
            List<String> players = new ArrayList<>();
            players.add(username);
            LobbyInfoMongoDB newLobby = new LobbyInfoMongoDB("", map, players, 0);
            lobbyCollection.save(newLobby);
            return new JoinLobbyResponse(LobbyTypeResponse.WAITING, newLobby.getId());
        } else {
            this.logger.debug("The lobby list has some entries!");
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
        if (lobby.getMatchId().isEmpty() || lobby.getMatchId() == null) {
            
            MatchInfoMongoDB match = matchCollection.save(
                new MatchInfoMongoDB("", 
                    lobby.getPlayers(), 
                    null, 
                    System.currentTimeMillis()));

            lobby.setMatchId(match.getId());
            lobby.setCounter(lobby.getCounter() + 1);
            lobbyCollection.save(lobby);

            String createGameServer = mapper.writeValueAsString(new ManagerCreateServer(match.getId(), lobby.getMap()));
            /* SYNC */
            ResponseEntity<String> result = RestClient
                .create(GAMESERVER_DIR)
                .post()
                .uri(new URI(CREATE_GAMESERVER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(createGameServer)
                .retrieve()
                .toEntity(String.class);


            // TODO: check https://docs.spring.io/spring-framework/reference/web/webflux-webclient/client-retrieve.html
            /*
            CompletableFuture<?> result = WebClient
                .create(GAMESERVER_DIR)
                .post()
                .uri(new URI(CREATE_GAMESERVER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .body(createGameServer)
                .retrieve()
                .bodyToMono(Object.class).toFuture();
            */
            GameServerInfo gameServerInfo = mapper.readValue(result.getBody(), GameServerInfo.class);
            this.mongoBackground.saveMatchInfo(match, gameServerInfo, matchCollection);
        }
        JoinLobbyResponse response = new JoinLobbyResponse(LobbyTypeResponse.FOUND, lobby.getMatchId());
        lobby.setCounter(lobby.getCounter() + 1);
        lobbyCollection.save(lobby);
        this.mongoBackground.checkLobbyToDelete(lobby, lobbyCollection, LOBBY_SIZE);
        return response;
    }

    /**
     * Returns the information for the specified match.
     * @param matchId the identifier of the match.
     * @return the MatchInfoMongoDB when it exists or throws an Exception because the match is not in the database.  
     * @throws Exception
     */
    public MatchInfoMongoDB getMatch(String matchId) throws Exception {
        return matchCollection.findById(matchId)
            .orElseThrow(() -> new Exception("Match does not exist!"));
    }

    /**
     * Searches for the last match joined by the user when it got kicked out because of a crash.
     * @param username of the player interested in re-joining the match.
     * @return the last match joined.
     * @throws Exception
     */
    public MatchInfoMongoDB getMatchByToken(String username) throws Exception {
        Optional<MatchInfoMongoDB> match = matchCollection
            .findByUsername(username)
            .stream()
            .sorted((x, y) -> Long.compare(x.getTimeOfCreation(), x.getTimeOfCreation()))
            .findFirst();
        if (match.isEmpty()) {
            throw new Exception("No active match found!");
        }
        return match.get();
    }

    /**
     * Deletes the match from the mongo db.
     * @param matchId the identifier of the match.
     */
    public void deleteMatch(String matchId) {
        this.matchCollection.deleteById(matchId);
    }

    /**
     * It deletes the player from the match.
     * @param matchId the identifier of the match.
     * @param username the identifier of the user who wants to quit the game.
     */
    public void deleteUserFromMatch(String matchId, String username) {
        this.matchCollection.removeUserFromMatch(matchId, username);
    }

    /**
     * Contacts the Manager to check the availability of the GameServer for the match.
     * @param match the match.
     * @return the information of the new GameServer to connect with or null if it's ok.
     * @throws Exception
     */
    public GameServerInfo checkGameServerAvailability(MatchInfoMongoDB match) throws Exception {
        Long timeLeft = this.matchCollection.getTimeLeft(match.getId());
        GameServerCheckRequest gameServerRequest = new GameServerCheckRequest(match.getGameServerName(), timeLeft);
        String request = mapper.writeValueAsString(gameServerRequest);

        ResponseEntity<String> result = RestClient
            .create(GAMESERVER_DIR)
            .post()
            .uri(new URI(CHECK_GAMESERVER_URI))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(String.class);

        GameServerCheckResponse checkResponse = mapper.readValue(result.getBody(), GameServerCheckResponse.class);
        if (checkResponse.status() == GameServerStatus.UNHEALTHY) {
            return checkResponse.serverInfo();
        }
        return null;
    }

    /**
     * Updates the match with the new informations about the GameServer.
     * @param match the match to update.
     * @param info the new informations about the GameServer.
     */
    public void setNewGameServerInfo(MatchInfoMongoDB match, GameServerInfo info) {
        this.mongoBackground.saveNewGameServerInfo(match, info, this.matchCollection);
    }

}