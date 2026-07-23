package it.unibo.mongodb;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * MatchInfoMongoDB
 */
@Document(collection = "matches")
public class MatchInfoMongoDB {
    
    @Id
    private String id;

    @Indexed(unique = true)
    private String matchId;

    private List<String> users;
    private Socket gameServerSocket;
    
    public MatchInfoMongoDB() {
    
    }
    
    public MatchInfoMongoDB(
        String matchId, 
        List<String> users, 
        Socket gameServerSocket) {
        this.matchId = matchId;
        this.users = users;
        this.gameServerSocket = gameServerSocket;
    }

    public String getId() {
        return id;
    }
    public String getMatchId() {
        return matchId;
    }
    public List<String> getUsers() {
        return users;
    }
    public Socket getGameServerSocket() {
        return gameServerSocket;
    }

}
