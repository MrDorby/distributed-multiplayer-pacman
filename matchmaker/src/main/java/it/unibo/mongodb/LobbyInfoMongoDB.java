package it.unibo.mongodb;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * LobbyInfoMongoDB
 */
@Document(collection = "lobby")
public class LobbyInfoMongoDB {
    
    @Id
    private String id;

    @Indexed(unique = true)
    private String lobbyId;
    private String map;
    private List<Players> players;
    
    public LobbyInfoMongoDB() {

    }

    public LobbyInfoMongoDB(
        String lobbyId,
        String map,
        List<Players> players
    ) {
        this.lobbyId = lobbyId;
        this.map = map;
        this.players = players;
    }
    
    public String getId() {
        return id;
    }
    public String getLobbyId() {
        return lobbyId;
    }
    public String getMap() {
        return map;
    }
    public List<Players> getPlayers() {
        return players;
    }
}
