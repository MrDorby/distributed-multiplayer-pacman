package it.unibo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
 * MatchmakerDetailsService
 */
@Service
public class MatchmakerDetailsService {

    private static final int LOBBY_SIZE = 4;
    
    @Autowired
    private ShortTermLobbyRepository lobbyCollection;

    @Autowired
    private ShortTermMatchRepository matchCollection;

    /**
     * 
     * @param lobbyId
     * @return
     */
    public LobbyInfoMongoDB getLobby(String lobbyId) throws Exception {
        return lobbyCollection.findById(lobbyId)
            .orElseThrow(() -> new Exception("Lobby does not exist!"));
    }

    /**
     * 
     * @param lobbyId
     * @param username
     */
    public void deleteUserByLobbyId(String lobbyId, String username) {
        lobbyCollection.removeUserFromLobby(lobbyId, username);
    }

    /**
     * 
     * @param username
     * @param map
     * @return
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
     * 
     * @param matchId
     * @return
     */
    public MatchInfoMongoDB getMatch(String matchId) {
        return matchCollection.findByMatchId(matchId).orElse(null);
    }

}