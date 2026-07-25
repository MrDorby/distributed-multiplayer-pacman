package it.unibo.mongodb;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * Defines the structure for objects contained in the Lobby collection.
 */
@Document(collection = "lobby")
public class LobbyInfoMongoDB {
    
    @Id
    private String id;
    private String map;
    private List<String> players;
    
    public LobbyInfoMongoDB() {

    }

    public LobbyInfoMongoDB(
        String map,
        List<String> players
    ) {
        this.map = map;
        this.players = players;
    }
    
    public String getId() {
        return id;
    }

    public String getMap() {
        return map;
    }

    public List<String> getPlayers() {
        return players;
    }
}
