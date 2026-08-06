package it.unibo.mongodb;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.unibo.dto.GameServerInfo;

/**
 * MongoBackground manages the execution of asynchronous 
 * operations without blocking the main thread.
 */
@Service
public class MongoBackground {
    
    /**
     * It saves the match informations on db.
     * @param match the object to store on the db.
     * @param gameServerInfo the response of the GameServerManager.
     * @param matchCollection the match repository.
     */
    @Async
    public void saveMatchInfo(
        MatchInfoMongoDB match,
        GameServerInfo gameServerInfo,
        ShortTermMatchRepository matchCollection) {

        match.setGameServerName(gameServerInfo.name());
        match.setServerParameters(
            new ServerParameters(gameServerInfo.ip(), gameServerInfo.tcpPort(), gameServerInfo.udpPort()));
        matchCollection.save(match);
    }

    /**
     * Checks if it is necessary to delete the lobby on the db.
     * @param lobbyId the lobby informations.
     * @param lobbyCollection the repository with the lobby collection.
     * @param lobbySize the size of the lobby.
     */
    @Async
    public void checkLobbyToDelete(
        LobbyInfoMongoDB lobby, 
        ShortTermLobbyRepository lobbyCollection,
        int lobbySize) {
        if (lobby.getCounter() == lobbySize) {
            lobbyCollection.deleteById(lobby.getId());
        }
    }

    /**
     * Saves new match informations when the previous GameServer is collapsed. 
     * @param match the match to update.
     * @param info the new information about the GameServer.
     * @param repository the short term repository, matches collection.
     */
    @Async
    public void saveNewGameServerInfo(
        MatchInfoMongoDB match, 
        GameServerInfo info,
        ShortTermMatchRepository repository) {
        match.setGameServerName(info.name());
        match.setServerParameters(new ServerParameters(info.ip(), info.tcpPort(), info.udpPort()));
        repository.save(match);
    }
}
