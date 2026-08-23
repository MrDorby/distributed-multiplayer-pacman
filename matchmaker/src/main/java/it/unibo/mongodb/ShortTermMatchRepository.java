package it.unibo.mongodb;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

//TODO: Check: 
// https://docs.spring.io/spring-data/mongodb/reference/repositories/query-methods-details.html#repositories.query-async

/**
 * 
 * The short-term repository interface used for matches informations.
 */
@Repository
public interface ShortTermMatchRepository extends MongoRepository<MatchInfoMongoDB, String>{
    
    /**
     * Finds the all the informations about a specific match.
     * @param matchId the identifier of the match.
     * @return an Optional for a MatchInfoMongoDB.
     */
    Optional<MatchInfoMongoDB> findById(String matchId);

    /**
     * Finds all the matches containing the specific username.
     * @param username the user that it needed to find.
     * @return the list containing all the matches found.
     */
    @Query("{ 'users' : ?0 }")
    List<MatchInfoMongoDB> findByUsername(String username);

    /**
     * Finds the match with the current lobby identifier (specific for the lobby of players).
     * @param lobbyId the identifier of the lobby.
     * @return the match info.
     */
    Optional<MatchInfoMongoDB> findByLobbyId(String lobbyId);

    /**
     * Checks if there are other matches with the same players.
     * @param users the list of players.
     * @return the list containing all the matches found.
     */
    @Query("{ $expr: { $setEquals: [ '$users', ?0 ] } }")
    List<MatchInfoMongoDB> findByUsers(List<String> users);

    /**
     * Removes a user from the specific match when the user identified 
     * by the username decides to exit the game.
     * @param matchId is the identifier of the match.
     * @param username the identifier of the player.
     */
    @Query("{ '_id' : ?0 }")
    @Update("{ '$pull' : { 'users' : ?1 }}")
    void removeUserFromMatch(String matchId, String username);

    /**
     * Gets the time left from the specified match.
     * @param matchId the identifier of the match.
     * @return a Long representing the timeLeftInMillis.
     */
    @Query(value = "{ '_id' : ?0 }", fields = "{ 'checkpoints.gamecontext.gamestate.timeLeftInMillis' : 1, '_id' : 0 }")
    Optional<Long> getTimeLeft(String matchId);
}
