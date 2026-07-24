package it.unibo.mongodb;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

/**
 * 
 * ShortTermLobbyRepository
 */
@Repository
public interface ShortTermLobbyRepository extends MongoRepository<LobbyInfoMongoDB, String> {

    /**
     * 
     * @param lobbyId
     * @param username
     */
    @Query("{ '_id' : ?0 }")
    @Update("{ '$pull' : { 'players' : ?1 }}")
    void removeUserFromLobby(String lobbyId, String username);

    /**
     * 
     * @param id
     * @return
     */
    Optional<LobbyInfoMongoDB> findById(String id);

    /**
     * 
     * @param username
     * @return
     */
    Optional<LobbyInfoMongoDB> findByUsername(String username);

    /**
     * 
     * @param map
     * @return
     */
    List<LobbyInfoMongoDB> findByMap(String map);
    
}
