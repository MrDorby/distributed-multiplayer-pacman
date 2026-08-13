package it.unibo.mongodb;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

/**
 * 
 * The short-term repository interface used for lobbies.
 */
@Repository
public interface ShortTermLobbyRepository extends MongoRepository<LobbyInfoMongoDB, String> {

    /**
     * Removes a user from the specific lobby queue when the user identified 
     * by the username decides to quit the lobby.
     * @param lobbyId is the identifier of the lobby.
     * @param username the identifier of the player.
     */
    @Query("{ '_id' : ?0 }")
    @Update("{ '$pull' : { 'players' : ?1 }}")
    void removeUserFromLobby(String lobbyId, String username);

    /**
     * Find a LobbyInfoMongoDB object in the database searching it by means
     * of its id.
     * @param id the identifier of the specific object stored on the mongo database.
     * @return an Optional of LobbyInfoMongoDB.
     */
    Optional<LobbyInfoMongoDB> findById(String id);

    /**
     * Checks if a lobby containing the specific user exists.
     * @param username the identifier for the query.
     * @return an Optional of LobbyInfoMongoDB.
     */
    @Query("{ 'players' : ?0 }")
    Optional<LobbyInfoMongoDB> findByUsername(String username);

    /**
     * Returns the list of all lobbies for a specific map.
     * @param map the name of the map.
     * @return a List of all the LobbyInfoMongoDB objects.
     */
    List<LobbyInfoMongoDB> findByMap(String map);

    /**
     * Checks if exists a lobby with the desired matchId.
     * @param matchId the identifier of the match.
     * @return an Optional of LobbyInfoMongoDB.
     */
    Optional<LobbyInfoMongoDB> findByMatchId(String matchId);
    
}
