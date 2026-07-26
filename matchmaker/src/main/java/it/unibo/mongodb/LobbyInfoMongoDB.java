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
    
    /**
     * @return the lobby identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the map name.
     */
    public String getMap() {
        return map;
    }

    /**
     * @return the list of the players' names.
     */
    public List<String> getPlayers() {
        return players;
    }
}
