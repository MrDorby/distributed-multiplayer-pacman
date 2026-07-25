package it.unibo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.unibo.dto.JoinLobbyResponse;
import it.unibo.dto.LobbyTypeResponse;
import it.unibo.mongodb.LobbyInfoMongoDB;
import it.unibo.mongodb.MatchInfoMongoDB;
import it.unibo.mongodb.ShortTermLobbyRepository;
import it.unibo.mongodb.ShortTermMatchRepository;

/**
 * 
 * Manages all the actions required by the Matchmaker.
 */
@Service
public class MatchmakerDetailsService {

    private static final int LOBBY_SIZE = 4;
    
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
                new JoinLobbyResponse(LobbyTypeResponse.FOUND, lobby.get().getId());
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
                new JoinLobbyResponse(LobbyTypeResponse.FOUND, newLobby.getId()) : 
                new JoinLobbyResponse(LobbyTypeResponse.WAITING, newLobby.getId());
        }
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