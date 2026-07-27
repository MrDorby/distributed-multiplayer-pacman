package it.unibo.mongodb;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * Defines the structure for objects contained in the Lobby collection.
 */
@Document(collection = "lobby")
public class LobbyInfoMongoDB {
    
    @Id
    private String id;

    @Indexed(unique = true)
    private String matchId;
    private String map;
    private List<String> players;
    private int counter;

    public LobbyInfoMongoDB() {

    }

    public LobbyInfoMongoDB(
        String matchId,
        String map,
        List<String> players,
        int counter
    ) {
        this.matchId = matchId;
        this.map = map;
        this.players = players;
        this.counter = counter;
    }

    /**
     * @return the lobby identifier.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the id.
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the matchId.
     */
    public String getMatchId() {
        return matchId;
    }

    /**
     * Sets the match identifier.
     * @param matchId
     */
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    /**
     * @return the map name.
     */
    public String getMap() {
        return map;
    }

    /**
     * Sets the map for the lobby.
     * @param map
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * @return the list of the players' names.
     */
    public List<String> getPlayers() {
        return players;
    }

    /**
     * Sets the list of players waiting in the lobby queue.
     * @param players
     */
    public void setPlayers(List<String> players) {
        this.players = players;
    }

    /**
     * @return the counter for the number of users that received the FOUND packet.
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Sets the counter to check how many users have requested the GameServer infos.
     * @param counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }
}
