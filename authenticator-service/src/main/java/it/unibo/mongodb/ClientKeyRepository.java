package it.unibo.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the pairs key=username, value=public key of the player.
 */
@Repository
public interface ClientKeyRepository extends MongoRepository<ClientKeyMongoDB, String>{

    /**
     * Finds the pair username, public key by the means of the username.
     * @param username the identifier of the player.
     * @return an Optional of ClientKeyMongo for the user.
     */
    Optional<ClientKeyMongoDB> findByUsername(String username);

}