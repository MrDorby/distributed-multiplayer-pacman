package it.unibo.mongodb;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
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

    @Indexed(unique = true)
    private String lobbyId;
    private String gameServerName;
    private List<String> users;
    private ServerParameters serverParameters;
    private Long timeOfCreation;
    private List<Checkpoint> checkpoints;

    @Version
    private Long version;

    public MatchInfoMongoDB() {
    
    }

    public MatchInfoMongoDB(
        String lobbyId,
        String gameServerName,
        List<String> users, 
        ServerParameters serverParameters,
        Long timeOfCreation) {
        this.gameServerName = gameServerName;
        this.users = users;
        this.serverParameters = serverParameters;
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
     * @return lobby id of the document.
     */
    public String getLobbyId() {
        return this.lobbyId;
    }

    /**
     * @return the GameServer's unique name.
     */
    public String getGameServerName() {
        return gameServerName;
    }

    /**
     * Sets the unique name of the GameServer.
     * @param gameServerName the name.
     */
    public void setGameServerName(String gameServerName) {
        this.gameServerName = gameServerName;
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
    public ServerParameters getServerParameters() {
        return serverParameters;
    }

    /**
     * Sets the infos about the GameServer. 
     * @param gameServerSocket
     */
    public void setServerParameters(ServerParameters gameServerSocket) {
        this.serverParameters = gameServerSocket;
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

    /**
     * @return the checkpoints.
     */
    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Sets the list of the checkpoints.
     * @param checkpoints
     */
    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
