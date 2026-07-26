package it.unibo.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
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
    Optional<MatchInfoMongoDB> findByMatchId(String matchId);
}
