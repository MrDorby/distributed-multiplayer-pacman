package it.unibo.mongodb;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * MongoBackground manages the execution of asynchronous 
 * operations without blocking the main thread.
 */
@Service
public class MongoBackground {
    
    /**
     * It saves the match informations on db.
     * @param match the object to store on the db.
     * @param gameServer the socket information about the GameServer.
     * @param matchCollection the match repository.
     */
    @Async
    public void saveMatchInfo(
        MatchInfoMongoDB match,
        Socket gameServer,
        ShortTermMatchRepository matchCollection) {
            
        match.setGameServerSocket(gameServer);
        matchCollection.save(match);
    }

    /**
     * 
     * @param lobby
     * @param lobbyCollection
     */
    @Async
    public void checkLobbyToDelete(LobbyInfoMongoDB lobby, ShortTermLobbyRepository lobbyCollection) {
        lobby.setCounter(lobby.getCounter() + 1);
        if (lobby.getCounter() == lobby.getPlayers().size()) {
            lobbyCollection.delete(lobby);
        } else {
            lobbyCollection.save(lobby);
        }
    }
}
