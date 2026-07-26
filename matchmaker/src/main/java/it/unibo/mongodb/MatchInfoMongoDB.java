package it.unibo.mongodb;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * Defines the structure for objects contained in the Matches collection.
 */
@Document(collection = "matches")
public class MatchInfoMongoDB {
    
    @Id
    private String id;
    private List<String> users;
    private Socket gameServerSocket;
    
    public MatchInfoMongoDB() {
    
    }

    public MatchInfoMongoDB(
        List<String> users, 
        Socket gameServerSocket) {
        this.users = users;
        this.gameServerSocket = gameServerSocket;
    }

    /**
     * @return the matchId.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the list of the players' names.
     */
    public List<String> getUsers() {
        return users;
    }
    
    /**
     * @return the infos about the GameServer.
     */
    public Socket getGameServerSocket() {
        return gameServerSocket;
    }
}
