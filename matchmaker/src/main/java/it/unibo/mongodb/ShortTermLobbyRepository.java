package it.unibo.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
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
     * @return
     */
    Optional<LobbyInfoMongoDB> findByLobbyID(String lobbyId);
    
}
