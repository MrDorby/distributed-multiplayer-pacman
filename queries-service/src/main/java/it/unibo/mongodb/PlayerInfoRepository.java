package it.unibo.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerInfoRepository extends MongoRepository<PlayerInfoMongoDB, String>{
    
    Optional<PlayerInfoMongoDB> findByUsername(String username);
}
