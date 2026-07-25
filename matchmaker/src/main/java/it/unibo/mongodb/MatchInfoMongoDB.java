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
    private Socket gameServerSocket; //TODO: Change because we need to understand
    // how the information is stored.
    
    public MatchInfoMongoDB() {
    
    }

    public MatchInfoMongoDB(
        List<String> users, 
        Socket gameServerSocket) {
        this.users = users;
        this.gameServerSocket = gameServerSocket;
    }

    public String getId() {
        return id;
    }

    public List<String> getUsers() {
        return users;
    }
    
    public Socket getGameServerSocket() {
        return gameServerSocket;
    }
}
