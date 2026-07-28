package it.unibo.mongodb;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
    private Long timeOfCreation;

    public MatchInfoMongoDB() {
    
    }

    public MatchInfoMongoDB(
        List<String> users, 
        Socket gameServerSocket,
        Long timeOfCreation) {
        this.users = users;
        this.gameServerSocket = gameServerSocket;
        this.timeOfCreation = timeOfCreation;
    }

    /**
     * @return id of the document.
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
     * @return the list of the players' names.
     */
    public List<String> getUsers() {
        return users;
    }

    /**
     * Sets the list of the users.
     * @param users
     */
    public void setUsers(List<String> users) {
        this.users = users;
    }
    
    /**
     * @return the infos about the GameServer.
     */
    public Socket getGameServerSocket() {
        return gameServerSocket;
    }

    /**
     * Sets the infos about the GameServer. 
     * @param gameServerSocket
     */
    public void setGameServerSocket(Socket gameServerSocket) {
        this.gameServerSocket = gameServerSocket;
    }

    /**
     * @return the timestamp indicating when the match was created.
     */
    public Long getTimeOfCreation() {
        return timeOfCreation;
    }

    /**
     * Sets the time of creation for the specified match.
     * @param timeOfCreation
     */
    public void setTimeOfCreation(Long timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }
}
