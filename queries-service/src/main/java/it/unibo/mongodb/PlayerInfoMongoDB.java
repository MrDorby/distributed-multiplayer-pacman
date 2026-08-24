package it.unibo.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * PlayerInfoMongoDB
 */
@Document(collection = "user")
public class PlayerInfoMongoDB {
    
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    private int nMatch;
    private int nWins;
    private int bestScore;

    public PlayerInfoMongoDB() {

    }

    public PlayerInfoMongoDB(
        String username, 
        int nMatch, 
        int nWins, 
        int bestScore) {
        this.username = username;
        this.nMatch = nMatch;
        this.nWins = nWins;
        this.bestScore = bestScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getnMatch() {
        return nMatch;
    }

    public void setnMatch(int nMatch) {
        this.nMatch = nMatch;
    }

    public int getnWins() {
        return nWins;
    }

    public void setnWins(int nWins) {
        this.nWins = nWins;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }
}
