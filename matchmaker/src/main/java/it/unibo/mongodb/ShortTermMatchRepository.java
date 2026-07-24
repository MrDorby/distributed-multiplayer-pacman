package it.unibo.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//TODO: Check: 
// https://docs.spring.io/spring-data/mongodb/reference/repositories/query-methods-details.html#repositories.query-async

/**
 * 
 * ShortTermMatchRepository
 */
@Repository
public interface ShortTermMatchRepository extends MongoRepository<MatchInfoMongoDB, String>{
    
    /**
     * 
     * @param matchId
     * @return
     */
    Optional<MatchInfoMongoDB> findByMatchId(String matchId);
}
