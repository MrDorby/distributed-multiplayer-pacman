package it.unibo.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.unibo.dto.GameServerInfo;

/**
 * MongoBackground manages the execution of asynchronous 
 * operations without blocking the main thread.
 */
@Service
public class MongoBackground {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * It saves the match informations on db.
     * @param match the object to store on the db.
     * @param gameServerInfo the response of the GameServerManager.
     * @param matchCollection the match repository.
     */
    @Async("threadPoolExecutor")
    public void saveMatchInfo(
        MatchInfoMongoDB match,
        GameServerInfo gameServerInfo,
        ShortTermMatchRepository matchCollection) {

        Query query = new Query(Criteria.where("id").is(match.getId()));
        Update update = new Update()
            .set("gameServerName", gameServerInfo.name())
            .set("serverParameters", new ServerParameters(gameServerInfo.ip(), gameServerInfo.tcpPort(), gameServerInfo.udpPort()));
        
        mongoTemplate.updateFirst(query, update, MatchInfoMongoDB.class);
    }

    /**
     * Checks if it is necessary to delete the lobby on the db.
     * @param lobbyId the lobby identifier.
     * @param lobbyCollection the repository with the lobby collection.
     * @param lobbySize the size of the lobby.
     */
    @Async("threadPoolExecutor")
    public void checkLobbyToDelete(
        String lobbyId, 
        ShortTermLobbyRepository lobbyCollection,
        int lobbySize) {
        
        Query query = new Query(Criteria.where("id").is(lobbyId));
        Update update = new Update()
            .inc("counter", 1);
        
        LobbyInfoMongoDB updatedLobby = mongoTemplate
            .findAndModify(
                query, 
                update, 
                FindAndModifyOptions.options().returnNew(true), 
                LobbyInfoMongoDB.class
        );

        if (updatedLobby != null && 
            updatedLobby.getMatchId() != null && 
            (updatedLobby.getCounter() >= lobbySize || updatedLobby.getPlayers().isEmpty())
        ) {
            lobbyCollection.deleteById(lobbyId);
        }
    }

    /**
     * Saves new match informations when the previous GameServer is collapsed. 
     * @param match the match to update.
     * @param info the new information about the GameServer.
     * @param repository the short term repository, matches collection.
     */
    @Async("threadPoolExecutor")
    public void saveNewGameServerInfo(
        MatchInfoMongoDB match, 
        GameServerInfo info,
        ShortTermMatchRepository repository) {
        
        Query query = new Query(Criteria.where("id").is(match.getId()));
        Update update = new Update()
            .set("gameServerName", info.name())
            .set("serverParameters", new ServerParameters(info.ip(), info.tcpPort(), info.udpPort()));
        
        mongoTemplate.updateFirst(query, update, MatchInfoMongoDB.class);
    }
}
