package it.unibo.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for long term data.
 */
@Repository
public interface AuthRepository extends MongoRepository<AuthMongoDB, String>{

    /**
     * Finds a specific user by its username.
     * @param username the identifier of the player.
     * @return an Optional containing the AuthMongo of the user.
     */
    Optional<AuthMongoDB> findByUsername(String username);

}