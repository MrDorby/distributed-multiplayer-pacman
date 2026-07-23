package it.unibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ShortTermLobbyRepository lobbyCollection;

    @Autowired
    private ShortTermMatchRepository matchCollection;

    /**
     * 
     * @param lobbyId
     * @return
     */
    public LobbyInfoMongoDB getLobby(String lobbyId) {
        return lobbyCollection.findByLobbyID(lobbyId).orElse(null);
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